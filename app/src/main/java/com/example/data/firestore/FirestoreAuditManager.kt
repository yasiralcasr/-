package com.example.data.firestore

import android.content.Context
import android.util.Log
import com.example.data.model.AppLanguage
import com.example.data.model.AuditLogEntry
import com.example.data.model.LogSeverity
import com.example.data.model.RoleRank
import com.example.data.model.UserAccount
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

/**
 * Cloud Firestore Audit Log Model
 * Represents a secure immutable record of administrative actions taken by high-privilege accounts.
 */
data class FirestoreAuditLog(
    val id: String = "",
    val timestamp: String = "",
    val epochMillis: Long = System.currentTimeMillis(),
    val actorId: String = "",
    val actorEmail: String = "",
    val actorName: String = "",
    val actorRole: String = "",
    val actorRankLevel: Int = 1,
    val actionAr: String = "",
    val actionEn: String = "",
    val level: String = LogSeverity.INFO.name,
    val targetUserId: String = "",
    val targetUserName: String = "",
    val details: String = "",
    val clientPlatform: String = "Android EastWest OS 2026",
    val isRootAction: Boolean = false,
    val isMasterOverride: Boolean = false
) {
    fun toMap(): Map<String, Any> = mapOf(
        "id" to id,
        "timestamp" to timestamp,
        "epochMillis" to epochMillis,
        "actorId" to actorId,
        "actorEmail" to actorEmail,
        "actorName" to actorName,
        "actorRole" to actorRole,
        "actorRankLevel" to actorRankLevel,
        "actionAr" to actionAr,
        "actionEn" to actionEn,
        "level" to level,
        "targetUserId" to targetUserId,
        "targetUserName" to targetUserName,
        "details" to details,
        "clientPlatform" to clientPlatform,
        "isRootAction" to isRootAction,
        "isMasterOverride" to isMasterOverride
    )

    companion object {
        fun fromDocument(doc: DocumentSnapshot): FirestoreAuditLog {
            return FirestoreAuditLog(
                id = doc.getString("id") ?: doc.id,
                timestamp = doc.getString("timestamp") ?: "",
                epochMillis = doc.getLong("epochMillis") ?: System.currentTimeMillis(),
                actorId = doc.getString("actorId") ?: "",
                actorEmail = doc.getString("actorEmail") ?: "",
                actorName = doc.getString("actorName") ?: "Unknown Admin",
                actorRole = doc.getString("actorRole") ?: "Administrator",
                actorRankLevel = (doc.getLong("actorRankLevel") ?: 1L).toInt(),
                actionAr = doc.getString("actionAr") ?: "",
                actionEn = doc.getString("actionEn") ?: "",
                level = doc.getString("level") ?: LogSeverity.INFO.name,
                targetUserId = doc.getString("targetUserId") ?: "",
                targetUserName = doc.getString("targetUserName") ?: "",
                details = doc.getString("details") ?: "",
                clientPlatform = doc.getString("clientPlatform") ?: "Android EastWest OS",
                isRootAction = doc.getBoolean("isRootAction") ?: false,
                isMasterOverride = doc.getBoolean("isMasterOverride") ?: false
            )
        }
    }
}

/**
 * Manages auditing of high-privilege executive operations and synchronization with Google Cloud Firestore.
 */
class FirestoreAuditManager private constructor(private val appContext: Context) {

    private val firestore: FirebaseFirestore? by lazy {
        try {
            if (FirebaseApp.getApps(appContext).isEmpty()) {
                FirebaseApp.initializeApp(appContext)
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
        } catch (e: Exception) {
            Log.w(TAG, "Firestore initialization warning: ${e.localizedMessage}")
            null
        }
    }

    private val auth: FirebaseAuth? by lazy {
        try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Record an administrative event directly to Firestore
     */
    suspend fun recordAdminAction(
        actorUser: UserAccount?,
        actionAr: String,
        actionEn: String,
        level: LogSeverity = LogSeverity.COMMAND,
        targetUser: UserAccount? = null,
        details: String = "",
        isRootKeyUsed: Boolean = false
    ): Boolean = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext false
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val nowStr = sdf.format(Date())
            val logId = "audit_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().take(6)
            val currentAuthUser = auth?.currentUser

            val actorId = actorUser?.id ?: currentAuthUser?.uid ?: "sys-admin-root"
            val actorEmail = currentAuthUser?.email ?: if (isRootKeyUsed) "root.sovereign@eastwest.com" else "admin@eastwest.com"
            val actorName = if (isRootKeyUsed) {
                "ياسر الرشيدي (Group CEO • الرئيس التنفيذي)"
            } else {
                actorUser?.fullName ?: (currentAuthUser?.displayName ?: "High-Privilege Officer")
            }
            val actorRole = if (isRootKeyUsed) {
                "Group CEO & President (Root Authority)"
            } else {
                actorUser?.roleRank?.titleEn ?: "Administrator"
            }
            val actorRankLevel = if (isRootKeyUsed) 6 else (actorUser?.roleRank?.level ?: 5)

            val log = FirestoreAuditLog(
                id = logId,
                timestamp = nowStr,
                epochMillis = System.currentTimeMillis(),
                actorId = actorId,
                actorEmail = actorEmail,
                actorName = actorName,
                actorRole = actorRole,
                actorRankLevel = actorRankLevel,
                actionAr = actionAr,
                actionEn = actionEn,
                level = level.name,
                targetUserId = targetUser?.id ?: "",
                targetUserName = targetUser?.fullName ?: "",
                details = details,
                clientPlatform = "Android EastWest Sovereign Engine v2.4",
                isRootAction = isRootKeyUsed || actorRankLevel >= 5 || level == LogSeverity.MASTER_OVERRIDE,
                isMasterOverride = isRootKeyUsed || (actorUser?.isMasterOverride == true)
            )

            db.collection(COLLECTION_NAME)
                .document(logId)
                .set(log.toMap())
                .await()

            Log.d(TAG, "Firestore audit log recorded: $logId | $actionEn")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to record audit log to Firestore: ${e.localizedMessage}", e)
            false
        }
    }

    /**
     * Realtime Stream of Audit Logs from Firestore
     */
    fun getLiveAuditLogsFlow(): Flow<List<FirestoreAuditLog>> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listenerRegistration = db.collection(COLLECTION_NAME)
            .orderBy("epochMillis", Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Firestore audit listener error: ${error.localizedMessage}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val logs = snapshot.documents.mapNotNull { doc ->
                        try {
                            FirestoreAuditLog.fromDocument(doc)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(logs)
                }
            }

        awaitClose {
            listenerRegistration.remove()
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Synchronize a batch of Room audit logs to Firestore
     */
    suspend fun syncBatchToFirestore(entries: List<AuditLogEntry>): Int = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext 0
        var count = 0
        val currentAuthUser = auth?.currentUser
        try {
            val batch = db.batch()
            entries.take(30).forEach { entry ->
                val logId = "synced_" + entry.id
                val docRef = db.collection(COLLECTION_NAME).document(logId)
                val log = FirestoreAuditLog(
                    id = logId,
                    timestamp = entry.timestamp,
                    epochMillis = System.currentTimeMillis(),
                    actorId = currentAuthUser?.uid ?: "local-node",
                    actorEmail = currentAuthUser?.email ?: "local@eastwest.com",
                    actorName = entry.actorName,
                    actorRole = entry.actorRole,
                    actorRankLevel = if (entry.level == LogSeverity.MASTER_OVERRIDE) 6 else 4,
                    actionAr = entry.actionAr,
                    actionEn = entry.actionEn,
                    level = entry.level.name,
                    details = entry.details,
                    isRootAction = entry.level == LogSeverity.MASTER_OVERRIDE,
                    isMasterOverride = entry.level == LogSeverity.MASTER_OVERRIDE
                )
                batch.set(docRef, log.toMap())
                count++
            }
            batch.commit().await()
            Log.d(TAG, "Successfully synced $count audit logs to Firestore")
        } catch (e: Exception) {
            Log.w(TAG, "Batch sync warning: ${e.localizedMessage}")
        }
        count
    }

    companion object {
        private const val TAG = "FirestoreAuditManager"
        private const val COLLECTION_NAME = "audit_logs"

        @Volatile
        private var INSTANCE: FirestoreAuditManager? = null

        fun getInstance(context: Context): FirestoreAuditManager {
            return INSTANCE ?: synchronized(this) {
                val instance = FirestoreAuditManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
