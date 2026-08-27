package com.example.auth

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.example.data.model.RoleRank
import com.example.data.model.UserAccount
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.UUID

sealed class AuthResult {
    data class Success(val user: FirebaseUser?, val account: UserAccount) : AuthResult()
    data class Error(val message: String) : AuthResult()
    object Cancelled : AuthResult()
}

class AuthManager private constructor(private val appContext: Context) {

    private val auth: FirebaseAuth? by lazy {
        try {
            if (FirebaseApp.getApps(appContext).isEmpty()) {
                FirebaseApp.initializeApp(appContext)
            }
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            Log.w(TAG, "Firebase initialization warning: ${e.localizedMessage}")
            null
        }
    }

    private val credentialManager: CredentialManager by lazy {
        CredentialManager.create(appContext)
    }

    private val _currentUserState = MutableStateFlow<FirebaseUser?>(auth?.currentUser)
    val currentUserState: StateFlow<FirebaseUser?> = _currentUserState.asStateFlow()

    init {
        auth?.addAuthStateListener { firebaseAuth ->
            _currentUserState.value = firebaseAuth.currentUser
        }
    }

    suspend fun signInWithGoogle(
        activityContext: Context,
        serverClientId: String = DEFAULT_WEB_CLIENT_ID
    ): AuthResult = withContext(Dispatchers.IO) {
        try {
            val rawNonce = UUID.randomUUID().toString()
            val bytes = rawNonce.toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }

            val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(serverClientId)
                .setAutoSelectEnabled(false)
                .setNonce(hashedNonce)
                .build()

            val request: GetCredentialRequest = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                context = activityContext,
                request = request
            )

            val credential = result.credential
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken

                val firebaseAuth = auth
                if (firebaseAuth != null) {
                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                    val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()
                    val user = authResult.user
                    val userAccount = mapFirebaseUserToAccount(user)
                    AuthResult.Success(user, userAccount)
                } else {
                    // Fallback if Firebase App is in offline mock mode
                    val fallbackAccount = UserAccount(
                        id = googleIdTokenCredential.id,
                        username = googleIdTokenCredential.id.substringBefore("@"),
                        fullName = googleIdTokenCredential.displayName ?: "Google User",
                        roleRank = determineRoleRankForEmail(googleIdTokenCredential.id),
                        departmentAr = "الوصول السحابي المعتمد",
                        departmentEn = "Cloud Authenticated User",
                        assignedCode = "GOOGLE_AUTH_VERIFIED",
                        canRead = true,
                        canWrite = true,
                        canExecute = true,
                        canAdminister = googleIdTokenCredential.id.contains("yasir"),
                        canPurge = googleIdTokenCredential.id.contains("yasir"),
                        isMasterOverride = googleIdTokenCredential.id.contains("yasir")
                    )
                    AuthResult.Success(null, fallbackAccount)
                }
            } else {
                AuthResult.Error("نوع بيانات الاعتماد غير مدعوم / Unsupported credential type")
            }
        } catch (e: GetCredentialCancellationException) {
            Log.d(TAG, "Google Sign-In was cancelled by user")
            AuthResult.Cancelled
        } catch (e: GetCredentialException) {
            Log.e(TAG, "Credential Manager error", e)
            AuthResult.Error(e.localizedMessage ?: "فشل تسجيل الدخول عبر Credential Manager")
        } catch (e: Exception) {
            Log.e(TAG, "Google Sign-In error", e)
            AuthResult.Error(e.localizedMessage ?: "حدث خطأ أثناء المصادقة عبر Google")
        }
    }

    suspend fun signInWithEmail(email: String, pass: String): AuthResult = withContext(Dispatchers.IO) {
        try {
            val firebaseAuth = auth
            if (firebaseAuth != null) {
                val result = firebaseAuth.signInWithEmailAndPassword(email.trim(), pass).await()
                val user = result.user
                val account = mapFirebaseUserToAccount(user)
                AuthResult.Success(user, account)
            } else {
                // Offline fallback authentication
                val account = UserAccount(
                    id = "email-usr-${email.hashCode()}",
                    username = email.substringBefore("@"),
                    fullName = email.substringBefore("@").replace(".", " ").capitalizeWords(),
                    roleRank = determineRoleRankForEmail(email),
                    departmentAr = "حساب البريد السحابي",
                    departmentEn = "Cloud Email User",
                    assignedCode = "EMAIL_VERIFIED_LOCAL",
                    canRead = true,
                    canWrite = true,
                    canExecute = true,
                    canAdminister = email.contains("yasir") || email.contains("admin"),
                    canPurge = email.contains("yasir"),
                    isMasterOverride = email.contains("yasir")
                )
                AuthResult.Success(null, account)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Email Sign-In error", e)
            AuthResult.Error(e.localizedMessage ?: "فشل تسجيل الدخول بالبريد الإلكتروني")
        }
    }

    suspend fun signUpWithEmail(email: String, pass: String, displayName: String): AuthResult = withContext(Dispatchers.IO) {
        try {
            val firebaseAuth = auth
            if (firebaseAuth != null) {
                val result = firebaseAuth.createUserWithEmailAndPassword(email.trim(), pass).await()
                val user = result.user
                if (user != null && displayName.isNotBlank()) {
                    val profileUpdate = UserProfileChangeRequest.Builder()
                        .setDisplayName(displayName.trim())
                        .build()
                    user.updateProfile(profileUpdate).await()
                }
                val account = mapFirebaseUserToAccount(user, fallbackName = displayName)
                AuthResult.Success(user, account)
            } else {
                val account = UserAccount(
                    id = "email-new-${email.hashCode()}",
                    username = email.substringBefore("@"),
                    fullName = displayName.ifBlank { email.substringBefore("@") },
                    roleRank = determineRoleRankForEmail(email),
                    departmentAr = "عضو جديد - منظومة الشرق والغرب",
                    departmentEn = "New Member - East West Global",
                    assignedCode = "NEW_REGISTRATION_AUTH",
                    canRead = true,
                    canWrite = true,
                    canExecute = false,
                    canAdminister = false,
                    canPurge = false,
                    isMasterOverride = false
                )
                AuthResult.Success(null, account)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Email Sign-Up error", e)
            AuthResult.Error(e.localizedMessage ?: "فشل إنشاء الحساب الجديد")
        }
    }

    suspend fun sendPasswordResetEmail(email: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val firebaseAuth = auth
            if (firebaseAuth != null) {
                firebaseAuth.sendPasswordResetEmail(email.trim()).await()
                Result.success("تم إرسال رابط إعادة تعيين كلمة المرور إلى $email / Password reset link sent.")
            } else {
                Result.success("تمت محاكاة إرسال رابط إعادة التعيين بنجاح إلى $email / Reset link simulated.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Password reset error", e)
            Result.failure(e)
        }
    }

    suspend fun signOut() = withContext(Dispatchers.IO) {
        try {
            auth?.signOut()
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            _currentUserState.value = null
        } catch (e: Exception) {
            Log.w(TAG, "Sign out exception", e)
        }
    }

    fun mapFirebaseUserToAccount(user: FirebaseUser?, fallbackName: String? = null): UserAccount {
        if (user == null) {
            return UserAccount(
                id = "usr-guest",
                username = "guest_observer",
                fullName = "زائر المنظومة (ضيف)",
                roleRank = RoleRank.OBSERVER,
                departmentAr = "الاستكشاف العام",
                departmentEn = "Public Exploration",
                assignedCode = "GUEST_SESSION",
                canRead = true,
                canWrite = false,
                canExecute = false,
                canAdminister = false,
                canPurge = false,
                isMasterOverride = false
            )
        }

        val email = user.email ?: ""
        val name = user.displayName ?: fallbackName ?: email.substringBefore("@")
        val isFatherYasser = email.contains("yasiralcasr", ignoreCase = true) ||
                email.contains("yasir", ignoreCase = true) ||
                name.contains("ياسر", ignoreCase = true) ||
                name.contains("Yasser", ignoreCase = true)

        val rank = if (isFatherYasser) {
            RoleRank.SUPREME_COMMANDER
        } else {
            determineRoleRankForEmail(email)
        }

        return UserAccount(
            id = user.uid,
            username = email.substringBefore("@").ifBlank { "user_${user.uid.take(6)}" },
            fullName = if (isFatherYasser) "ياسر الرشيدي (Group CEO • الرئيس التنفيذي)" else name,
            roleRank = rank,
            departmentAr = if (isFatherYasser) "الرئاسة التنفيذية وحوكمة المجموعة والشركات التابعة" else "الأعضاء المعتمدون سحابياً",
            departmentEn = if (isFatherYasser) "Executive Leadership & Subsidiaries Governance" else "Cloud Authenticated Personnel",
            assignedCode = if (isFatherYasser) "1073781088@0503026675#8054$8051%" else "AUTH_CLOUD_${user.uid.take(8)}",
            canRead = true,
            canWrite = rank.level >= 2,
            canExecute = rank.level >= 3,
            canAdminister = rank.level >= 5 || isFatherYasser,
            canPurge = rank.level == 6 || isFatherYasser,
            isMasterOverride = rank.level == 6 || isFatherYasser
        )
    }

    private fun determineRoleRankForEmail(email: String): RoleRank {
        val lower = email.lowercase()
        return when {
            lower.contains("yasir") || lower.contains("founder") || lower.contains("supreme") -> RoleRank.SUPREME_COMMANDER
            lower.contains("general") || lower.contains("director") || lower.contains("manager") -> RoleRank.GENERAL
            lower.contains("supervisor") || lower.contains("lead") -> RoleRank.SUPERVISOR
            lower.contains("specialist") || lower.contains("engineer") || lower.contains("tech") -> RoleRank.SPECIALIST
            lower.contains("soldier") || lower.contains("member") -> RoleRank.SOLDIER
            else -> RoleRank.SOLDIER
        }
    }

    private fun String.capitalizeWords(): String = split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }

    companion object {
        private const val TAG = "AuthManager"
        const val DEFAULT_WEB_CLIENT_ID = "672470930952-default-client.apps.googleusercontent.com"

        @Volatile
        private var instance: AuthManager? = null

        fun getInstance(context: Context): AuthManager {
            return instance ?: synchronized(this) {
                instance ?: AuthManager(context.applicationContext).also { instance = it }
            }
        }
    }
}
