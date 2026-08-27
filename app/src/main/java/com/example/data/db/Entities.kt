package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "programs")
data class ProgramEntity(
    @PrimaryKey val id: String,
    val titleAr: String,
    val titleEn: String,
    val sectorAr: String,
    val sectorEn: String,
    val sectorType: String,
    val descriptionAr: String,
    val descriptionEn: String,
    val automationLevel: String,
    val status: String,
    val targetAudienceAr: String,
    val targetAudienceEn: String,
    val generatedUsername: String,
    val generatedPassword: String,
    val systemKey: String,
    val integrationEndpoints: String, // Comma separated
    val createdAt: String,
    val isApprovedForClients: Boolean = true
)

@Entity(tableName = "industrial_orders")
data class IndustrialOrderEntity(
    @PrimaryKey val orderId: String,
    val productCode: String,
    val productNameAr: String,
    val productNameEn: String,
    val clientName: String,
    val sectorType: String,
    val quantity: Int,
    val priority: String,
    val deliveryLocation: String,
    val contactEmail: String,
    val contactPhone: String,
    val notes: String,
    val status: String,
    val orderTimestamp: Long,
    val estimatedDeliveryDate: String
)

@Entity(tableName = "user_accounts")
data class UserAccountEntity(
    @PrimaryKey val id: String,
    val username: String,
    val fullName: String,
    val roleRank: String,
    val departmentAr: String,
    val departmentEn: String,
    val assignedCode: String,
    val canRead: Boolean,
    val canWrite: Boolean,
    val canExecute: Boolean,
    val canAdminister: Boolean,
    val canPurge: Boolean,
    val isMasterOverride: Boolean,
    val createdAt: String,
    val photoUrl: String = "",
    val imageReference: String = "",
    val bio: String = "",
    val phoneNumber: String = ""
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey val id: String,
    val timestamp: String,
    val actorName: String,
    val actorRole: String,
    val actionAr: String,
    val actionEn: String,
    val level: String,
    val details: String
)
