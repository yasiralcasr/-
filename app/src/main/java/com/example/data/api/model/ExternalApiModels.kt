package com.example.data.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Standard API Response Wrapper for External Platform Services
 */
@JsonClass(generateAdapter = false)
data class ApiResponseWrapper<T>(
    @Json(name = "success") val success: Boolean = true,
    @Json(name = "status") val status: Int = 200,
    @Json(name = "message") val message: String = "OK",
    @Json(name = "data") val data: T? = null,
    @Json(name = "timestamp") val timestamp: Long = System.currentTimeMillis()
)

/**
 * DTO for Industrial and Commercial Products fetched from external platform
 */
@JsonClass(generateAdapter = false)
data class ApiProductDto(
    @Json(name = "id") val id: String,
    @Json(name = "modelCode") val modelCode: String,
    @Json(name = "nameAr") val nameAr: String,
    @Json(name = "nameEn") val nameEn: String,
    @Json(name = "categoryAr") val categoryAr: String,
    @Json(name = "categoryEn") val categoryEn: String,
    @Json(name = "descriptionAr") val descriptionAr: String,
    @Json(name = "descriptionEn") val descriptionEn: String,
    @Json(name = "specsAr") val specsAr: List<String> = emptyList(),
    @Json(name = "specsEn") val specsEn: List<String> = emptyList(),
    @Json(name = "estimatedPriceUsd") val estimatedPriceUsd: Double = 0.0,
    @Json(name = "inStockQuantity") val inStockQuantity: Int = 0,
    @Json(name = "leadTimeDays") val leadTimeDays: Int = 1,
    @Json(name = "certStandards") val certStandards: String = "ISO-9001 / CE Industrial"
)

/**
 * DTO for Delegation, Power of Attorney and Wathq Services fetched from external platform
 */
@JsonClass(generateAdapter = false)
data class ApiDelegationServiceDto(
    @Json(name = "delegationId") val delegationId: String,
    @Json(name = "serviceCode") val serviceCode: String = "DELEGATION_SOVEREIGN",
    @Json(name = "principalName") val principalName: String,
    @Json(name = "authorizedPerson") val authorizedPerson: String,
    @Json(name = "nationalIdOrCr") val nationalIdOrCr: String,
    @Json(name = "scopeAr") val scopeAr: String,
    @Json(name = "scopeEn") val scopeEn: String,
    @Json(name = "platformName") val platformName: String,
    @Json(name = "issueDate") val issueDate: String,
    @Json(name = "expiryDate") val expiryDate: String,
    @Json(name = "status") val status: String,
    @Json(name = "verifiedSecuritySignature") val verifiedSecuritySignature: String = "",
    @Json(name = "details") val details: Map<String, String> = emptyMap()
)

/**
 * DTO for Wathq Government Record Verification
 */
@JsonClass(generateAdapter = false)
data class ApiWathqRecordDto(
    @Json(name = "recordId") val recordId: String,
    @Json(name = "serviceCode") val serviceCode: String,
    @Json(name = "titleAr") val titleAr: String,
    @Json(name = "titleEn") val titleEn: String,
    @Json(name = "queryNumber") val queryNumber: String,
    @Json(name = "status") val status: String,
    @Json(name = "entityName") val entityName: String,
    @Json(name = "issueDate") val issueDate: String,
    @Json(name = "expiryDate") val expiryDate: String,
    @Json(name = "details") val details: Map<String, String> = emptyMap()
)
