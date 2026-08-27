package com.example.data.model

data class UserAccount(
    val id: String,
    val username: String,
    val fullName: String,
    val roleRank: RoleRank,
    val departmentAr: String,
    val departmentEn: String,
    val assignedCode: String,
    val canRead: Boolean = true,
    val canWrite: Boolean = false,
    val canExecute: Boolean = false,
    val canAdminister: Boolean = false,
    val canPurge: Boolean = false,
    val isMasterOverride: Boolean = false,
    val createdAt: String = "2026-08-19",
    val photoUrl: String = "",
    val imageReference: String = "",
    val bio: String = "",
    val phoneNumber: String = ""
)

data class AuditLogEntry(
    val id: String,
    val timestamp: String,
    val actorName: String,
    val actorRole: String,
    val actionAr: String,
    val actionEn: String,
    val level: LogSeverity,
    val details: String
)

enum class LogSeverity(val labelAr: String, val labelEn: String, val colorHex: Long) {
    INFO("معلوماتي", "Info", 0xFF3B82F6),
    COMMAND("تنفيذ أمر", "Command", 0xFF10B981),
    WARNING("تنبيه أمني", "Warning", 0xFFF59E0B),
    MASTER_OVERRIDE("صلاحية مطلقة - كود السيادة", "Master Override", 0xFFD4AF37),
    CRITICAL("إجراء حاسم / جذري", "Critical Action", 0xFFEF4444)
}
