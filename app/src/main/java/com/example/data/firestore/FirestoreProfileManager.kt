package com.example.data.firestore

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.example.data.model.RoleRank
import com.example.data.model.UserAccount
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Cloud Firestore User Profile Entity
 * Represents an executive user profile stored and synchronized in Google Cloud Firestore.
 */
data class FirestoreUserProfile(
    val userId: String = "",
    val username: String = "",
    val fullName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val departmentAr: String = "",
    val departmentEn: String = "",
    val roleRankLevel: Int = 1,
    val roleRankTitle: String = "",
    val assignedCode: String = "",
    val photoUrl: String = "",
    val imageReference: String = "", // Can be file URI, Cloud Storage URL, or compact Base64 JPEG
    val imageSourceType: String = "CAMERA_OR_GALLERY", // CAMERA, GALLERY, PRESET, CLOUD
    val bio: String = "",
    val statusMessage: String = "",
    val lastUpdated: String = "",
    val updatedAtEpoch: Long = System.currentTimeMillis(),
    val isVerifiedExecutive: Boolean = true
) {
    fun toMap(): Map<String, Any> = mapOf(
        "userId" to userId,
        "username" to username,
        "fullName" to fullName,
        "email" to email,
        "phoneNumber" to phoneNumber,
        "departmentAr" to departmentAr,
        "departmentEn" to departmentEn,
        "roleRankLevel" to roleRankLevel,
        "roleRankTitle" to roleRankTitle,
        "assignedCode" to assignedCode,
        "photoUrl" to photoUrl,
        "imageReference" to imageReference,
        "imageSourceType" to imageSourceType,
        "bio" to bio,
        "statusMessage" to statusMessage,
        "lastUpdated" to lastUpdated,
        "updatedAtEpoch" to updatedAtEpoch,
        "isVerifiedExecutive" to isVerifiedExecutive
    )

    companion object {
        fun fromDocument(doc: DocumentSnapshot): FirestoreUserProfile {
            return FirestoreUserProfile(
                userId = doc.getString("userId") ?: doc.id,
                username = doc.getString("username") ?: "",
                fullName = doc.getString("fullName") ?: "",
                email = doc.getString("email") ?: "",
                phoneNumber = doc.getString("phoneNumber") ?: "",
                departmentAr = doc.getString("departmentAr") ?: "",
                departmentEn = doc.getString("departmentEn") ?: "",
                roleRankLevel = doc.getLong("roleRankLevel")?.toInt() ?: 1,
                roleRankTitle = doc.getString("roleRankTitle") ?: "",
                assignedCode = doc.getString("assignedCode") ?: "",
                photoUrl = doc.getString("photoUrl") ?: "",
                imageReference = doc.getString("imageReference") ?: "",
                imageSourceType = doc.getString("imageSourceType") ?: "CAMERA_OR_GALLERY",
                bio = doc.getString("bio") ?: "",
                statusMessage = doc.getString("statusMessage") ?: "",
                lastUpdated = doc.getString("lastUpdated") ?: "",
                updatedAtEpoch = doc.getLong("updatedAtEpoch") ?: System.currentTimeMillis(),
                isVerifiedExecutive = doc.getBoolean("isVerifiedExecutive") ?: true
            )
        }

        fun fromUserAccount(user: UserAccount, email: String = ""): FirestoreUserProfile {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            return FirestoreUserProfile(
                userId = user.id,
                username = user.username,
                fullName = user.fullName,
                email = email.ifEmpty { "${user.username}@eastwest-global.com" },
                phoneNumber = user.phoneNumber,
                departmentAr = user.departmentAr,
                departmentEn = user.departmentEn,
                roleRankLevel = user.roleRank.level,
                roleRankTitle = user.roleRank.titleAr,
                assignedCode = user.assignedCode,
                photoUrl = user.photoUrl,
                imageReference = user.imageReference,
                bio = user.bio,
                statusMessage = "السيادة والريادة العالمية في الأتمتة والتوريد الصناعي",
                lastUpdated = sdf.format(Date()),
                updatedAtEpoch = System.currentTimeMillis(),
                isVerifiedExecutive = user.roleRank.level >= 4
            )
        }
    }
}

/**
 * Firestore Profile Manager
 * Manages user profile customization, camera/gallery image persistence,
 * and seamless real-time syncing to Google Cloud Firestore.
 */
class FirestoreProfileManager private constructor(private val context: Context) {

    private val firestore: FirebaseFirestore by lazy {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Firebase initialization check: ${e.message}")
        }
        val db = FirebaseFirestore.getInstance()
        try {
            val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
            db.firestoreSettings = settings
        } catch (ignored: Exception) {
        }
        db
    }

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    /**
     * Save user profile to Cloud Firestore
     */
    suspend fun saveProfileToFirestore(profile: FirestoreUserProfile): Result<String> = withContext(Dispatchers.IO) {
        try {
            val userId = profile.userId.ifEmpty {
                auth.currentUser?.uid ?: "usr-default"
            }
            val targetProfile = profile.copy(
                userId = userId,
                lastUpdated = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
                updatedAtEpoch = System.currentTimeMillis()
            )

            // Save to primary user_profiles collection
            firestore.collection(COLLECTION_PROFILES)
                .document(userId)
                .set(targetProfile.toMap())
                .await()

            // Also keep users collection in sync
            try {
                firestore.collection("users")
                    .document(userId)
                    .set(targetProfile.toMap())
                    .await()
            } catch (e: Exception) {
                Log.w(TAG, "Secondary users sync: ${e.message}")
            }

            Log.i(TAG, "Successfully saved profile to Firestore for user: $userId")
            Result.success(userId)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving profile to Firestore: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Listen to real-time changes for a specific user profile from Firestore
     */
    fun getUserProfileFlow(userId: String): Flow<FirestoreUserProfile?> = callbackFlow {
        if (userId.isEmpty()) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val listener = firestore.collection(COLLECTION_PROFILES)
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Firestore profile listener error: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    try {
                        val profile = FirestoreUserProfile.fromDocument(snapshot)
                        trySend(profile)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing profile document: ${e.message}")
                    }
                } else {
                    trySend(null)
                }
            }

        awaitClose {
            listener.remove()
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Fetch user profile once from Firestore
     */
    suspend fun getProfileOnce(userId: String): FirestoreUserProfile? = withContext(Dispatchers.IO) {
        try {
            val doc = firestore.collection(COLLECTION_PROFILES).document(userId).get().await()
            if (doc.exists()) {
                FirestoreUserProfile.fromDocument(doc)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to get profile once: ${e.message}")
            null
        }
    }

    /**
     * Save camera captured Bitmap to local app storage and return local URI + Base64 Reference
     */
    suspend fun processCapturedCameraBitmap(
        userId: String,
        bitmap: Bitmap
    ): Pair<String, String> = withContext(Dispatchers.IO) {
        try {
            val avatarDir = File(context.filesDir, "profile_avatars").apply {
                if (!exists()) mkdirs()
            }
            val sanitizedUserId = userId.replace(Regex("[^a-zA-Z0-9_]"), "_")
            val targetFile = File(avatarDir, "avatar_${sanitizedUserId}.jpg")

            // Scale down if oversized to ensure responsive memory and cloud efficiency
            val scaledBitmap = scaleBitmapToMaxDimension(bitmap, 600)

            FileOutputStream(targetFile).use { out ->
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 88, out)
                out.flush()
            }

            val localFilePath = targetFile.absolutePath
            val base64Data = convertBitmapToBase64(scaledBitmap)
            val referenceString = "data:image/jpeg;base64,$base64Data"

            Pair(localFilePath, referenceString)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to process camera bitmap: ${e.message}", e)
            Pair("", "")
        }
    }

    /**
     * Save selected image URI from gallery/file picker to local app storage and return local URI + Base64 Reference
     */
    suspend fun processSelectedImageUri(
        userId: String,
        sourceUri: Uri
    ): Pair<String, String> = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(sourceUri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (originalBitmap != null) {
                processCapturedCameraBitmap(userId, originalBitmap)
            } else {
                Pair(sourceUri.toString(), sourceUri.toString())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to process image URI: ${e.message}", e)
            Pair(sourceUri.toString(), sourceUri.toString())
        }
    }

    /**
     * Convert Bitmap to compressed Base64 String for Firestore document embedding
     */
    fun convertBitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        val scaled = scaleBitmapToMaxDimension(bitmap, 480)
        scaled.compress(Bitmap.CompressFormat.JPEG, 82, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    private fun scaleBitmapToMaxDimension(bitmap: Bitmap, maxDim: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        if (width <= maxDim && height <= maxDim) return bitmap

        val ratio = width.toFloat() / height.toFloat()
        val targetWidth: Int
        val targetHeight: Int
        if (width > height) {
            targetWidth = maxDim
            targetHeight = (maxDim / ratio).toInt()
        } else {
            targetHeight = maxDim
            targetWidth = (maxDim * ratio).toInt()
        }
        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
    }

    companion object {
        private const val TAG = "FirestoreProfile"
        const val COLLECTION_PROFILES = "user_profiles"

        @Volatile
        private var INSTANCE: FirestoreProfileManager? = null

        fun getInstance(context: Context): FirestoreProfileManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: FirestoreProfileManager(context.applicationContext).also {
                    INSTANCE = it
                }
            }
        }
    }
}
