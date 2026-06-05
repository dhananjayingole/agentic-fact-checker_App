package eu.tutorials.fact_checker_app.data.api

import com.google.gson.annotations.SerializedName

// ── Request DTOs ──────────────────────────────────────────────

data class VerifyRequest(
    @SerializedName("claim") val claim: String,
    @SerializedName("max_sources") val maxSources: Int = 5
)

data class BatchVerifyRequest(
    @SerializedName("claims") val claims: List<String>,
    @SerializedName("max_sources") val maxSources: Int = 3
)

data class SearchRequest(
    @SerializedName("query") val query: String,
    @SerializedName("max_results") val maxResults: Int = 5
)

data class ExtractClaimsRequest(
    @SerializedName("text") val text: String
)

// ── Response DTOs ─────────────────────────────────────────────

data class EvidenceDto(
    @SerializedName("title") val title: String,
    @SerializedName("url") val url: String,
    @SerializedName("snippet") val snippet: String,
    @SerializedName("relevance_score") val relevanceScore: Double,
    @SerializedName("stance") val stance: String,
    @SerializedName("source_credibility") val sourceCredibility: String,
    @SerializedName("source_type") val sourceType: String
)

data class VerifyResponse(
    @SerializedName("claim") val claim: String,
    @SerializedName("verdict") val verdict: String,
    @SerializedName("confidence_score") val confidenceScore: Double,
    @SerializedName("evidence_count") val evidenceCount: Int,
    @SerializedName("evidence_list") val evidenceList: List<EvidenceDto>,
    @SerializedName("evidence_summary") val evidenceSummary: String,
    @SerializedName("reasoning") val reasoning: String,
    @SerializedName("processing_time_seconds") val processingTimeSeconds: Double,
    @SerializedName("sources_searched") val sourcesSearched: Int
)

data class BatchVerifyResponse(
    @SerializedName("results") val results: List<VerifyResponse>,
    @SerializedName("total_claims") val totalClaims: Int,
    @SerializedName("processing_time_seconds") val processingTimeSeconds: Double
)

data class SearchResultDto(
    @SerializedName("title") val title: String,
    @SerializedName("url") val url: String,
    @SerializedName("snippet") val snippet: String,
    @SerializedName("source_type") val sourceType: String
)

data class SearchResponse(
    @SerializedName("query") val query: String,
    @SerializedName("results") val results: List<SearchResultDto>,
    @SerializedName("total_results") val totalResults: Int
)

data class ExtractClaimsResponse(
    @SerializedName("original_text") val originalText: String,
    @SerializedName("claims") val claims: List<String>,
    @SerializedName("total_claims") val totalClaims: Int
)

data class HealthResponse(
    @SerializedName("status") val status: String,
    @SerializedName("version") val version: String,
    @SerializedName("services") val services: Map<String, String>
)