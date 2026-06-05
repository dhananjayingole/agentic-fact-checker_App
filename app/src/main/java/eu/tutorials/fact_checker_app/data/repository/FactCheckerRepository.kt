package eu.tutorials.fact_checker_app.data.repository

import eu.tutorials.fact_checker_app.data.api.ExtractClaimsRequest
import eu.tutorials.fact_checker_app.data.api.FactCheckerApiService
import eu.tutorials.fact_checker_app.data.api.VerifyRequest
import eu.tutorials.fact_checker_app.data.local.VerificationDao
import eu.tutorials.fact_checker_app.data.local.toDomain
import eu.tutorials.fact_checker_app.data.local.toEntity
import eu.tutorials.fact_checker_app.domain.CheckStats
import eu.tutorials.fact_checker_app.domain.ClaimResult
import eu.tutorials.fact_checker_app.domain.Verdict
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val code: Int? = null) : Result<Nothing>()
}

@Singleton
class FactCheckerRepository @Inject constructor(
    private val apiService: FactCheckerApiService,
    private val dao: VerificationDao
) {

    // ── Remote: Verify a claim ────────────────────────────────

    suspend fun verifyClaim(claim: String, maxSources: Int = 5): Result<ClaimResult> {
        return try {
            val response = apiService.verifyClaim(VerifyRequest(claim, maxSources))
            if (response.isSuccessful) {
                val body = response.body()!!
                val domainResult = body.toDomain()
                // Auto-save to local DB
                val savedId = dao.insert(domainResult.toEntity())
                Result.Success(domainResult.copy(id = savedId))
            } else {
                Result.Error(
                    message = "Server error: ${response.code()}",
                    code = response.code()
                )
            }
        } catch (e: Exception) {
            Result.Error(message = e.message ?: "Network error. Check your connection.")
        }
    }

    // ── Remote: Extract claims from text ─────────────────────

    suspend fun extractClaims(text: String): Result<List<String>> {
        return try {
            val response = apiService.extractClaims(ExtractClaimsRequest(text))
            if (response.isSuccessful) {
                Result.Success(response.body()!!.claims)
            } else {
                Result.Error("Failed to extract claims: ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    // ── Remote: Health check ──────────────────────────────────

    suspend fun checkHealth(): Result<Boolean> {
        return try {
            val response = apiService.healthCheck()
            if (response.isSuccessful && response.body()?.status == "ok") {
                Result.Success(true)
            } else {
                Result.Error("API not reachable")
            }
        } catch (e: Exception) {
            Result.Error("Cannot reach API: ${e.message}")
        }
    }

    // ── Local: History ────────────────────────────────────────

    fun getAllVerificationsFlow(): Flow<List<ClaimResult>> =
        dao.getAllFlow().map { list -> list.map { it.toDomain() } }

    fun searchVerificationsFlow(query: String): Flow<List<ClaimResult>> =
        dao.searchFlow(query).map { list -> list.map { it.toDomain() } }

    fun getByVerdictFlow(verdict: Verdict): Flow<List<ClaimResult>> =
        dao.getByVerdictFlow(verdict.name).map { list -> list.map { it.toDomain() } }

    fun getFavoritesFlow(): Flow<List<ClaimResult>> =
        dao.getFavoritesFlow().map { list -> list.map { it.toDomain() } }

    suspend fun getById(id: Long): ClaimResult? =
        dao.getById(id)?.toDomain()

    suspend fun deleteVerification(result: ClaimResult) =
        dao.delete(result.toEntity())

    suspend fun clearHistory() = dao.deleteAll()

    suspend fun toggleFavorite(id: Long, isFavorite: Boolean) =
        dao.setFavorite(id, isFavorite)

    // ── Local: Stats ──────────────────────────────────────────

    fun getStatsFlow(): Flow<CheckStats> = combine(
        dao.getTotalCount(),
        dao.getCountByVerdict(Verdict.TRUE.name),
        dao.getCountByVerdict(Verdict.FALSE.name),
        dao.getCountByVerdict(Verdict.INCONCLUSIVE.name)
    ) { total, trueCount, falseCount, inconclusiveCount ->
        CheckStats(
            total = total,
            trueCount = trueCount,
            falseCount = falseCount,
            inconclusiveCount = inconclusiveCount
        )
    }
}