package com.example.data.api

import com.example.data.api.model.ApiDelegationServiceDto
import com.example.data.api.model.ApiProductDto
import com.example.data.api.model.ApiResponseWrapper
import com.example.data.api.model.ApiWathqRecordDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit Interface for External Platform Services, Products Catalog, and Delegation Gateway
 */
interface ExternalPlatformApiService {

    /**
     * Fetch Industrial & Commercial Products catalog with optional category filtering
     */
    @GET("v1/products")
    suspend fun getProducts(
        @Query("category") category: String? = null,
        @Query("inStockOnly") inStockOnly: Boolean = false
    ): ApiResponseWrapper<List<ApiProductDto>>

    /**
     * Fetch specific product details by unique product ID or model code
     */
    @GET("v1/products/{id}")
    suspend fun getProductById(
        @Path("id") id: String
    ): ApiResponseWrapper<ApiProductDto>

    /**
     * Fetch authorized Delegation Services, Powers of Attorney, and Moqeem attestation data
     */
    @GET("v1/delegation/services")
    suspend fun getDelegationServices(
        @Query("query") query: String? = null,
        @Query("status") status: String? = null
    ): ApiResponseWrapper<List<ApiDelegationServiceDto>>

    /**
     * Fetch specific delegation record by delegation ID
     */
    @GET("v1/delegation/services/{delegationId}")
    suspend fun getDelegationById(
        @Path("delegationId") delegationId: String
    ): ApiResponseWrapper<ApiDelegationServiceDto>

    /**
     * Verify Wathq government services (Commercial Registration, Chamber of Commerce, Real Estate Deeds)
     */
    @GET("v1/wathq/verify")
    suspend fun queryWathqService(
        @Query("serviceCode") serviceCode: String,
        @Query("queryNumber") queryNumber: String
    ): ApiResponseWrapper<ApiWathqRecordDto>

    /**
     * Submit RFQ or Commercial Order to external enterprise ERP gateway
     */
    @POST("v1/orders/rfq")
    suspend fun submitExternalRfq(
        @Body payload: Map<String, String>
    ): ApiResponseWrapper<Map<String, String>>
}
