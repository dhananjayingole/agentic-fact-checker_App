package eu.tutorials.fact_checker_app.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface FactCheckerApiService {

    @POST("verify/")
    suspend fun verifyClaim(
        @Body request: VerifyRequest
    ): Response<VerifyResponse>

    @POST("verify/batch")
    suspend fun verifyBatch(
        @Body request: BatchVerifyRequest
    ): Response<BatchVerifyResponse>

    @POST("search/")
    suspend fun search(
        @Body request: SearchRequest
    ): Response<SearchResponse>

    @POST("extract/claims")
    suspend fun extractClaims(
        @Body request: ExtractClaimsRequest
    ): Response<ExtractClaimsResponse>

    @GET("health")
    suspend fun healthCheck(): Response<HealthResponse>
}