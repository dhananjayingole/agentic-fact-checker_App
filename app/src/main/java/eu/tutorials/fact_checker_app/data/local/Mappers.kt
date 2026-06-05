package eu.tutorials.fact_checker_app.data.local

import eu.tutorials.fact_checker_app.data.api.EvidenceDto
import eu.tutorials.fact_checker_app.data.api.VerifyResponse
import eu.tutorials.fact_checker_app.domain.ClaimResult
import eu.tutorials.fact_checker_app.domain.Evidence
import eu.tutorials.fact_checker_app.domain.Verdict

// ── DTO → Domain ──────────────────────────────────────────────

fun EvidenceDto.toDomain() = Evidence(
    title = title,
    url = url,
    snippet = snippet,
    relevanceScore = relevanceScore,
    stance = stance,
    sourceCredibility = sourceCredibility,
    sourceType = sourceType
)

fun VerifyResponse.toDomain(savedId: Long = 0L) = ClaimResult(
    id = savedId,
    claim = claim,
    verdict = Verdict.from(verdict),
    confidenceScore = confidenceScore,
    evidenceCount = evidenceCount,
    evidenceList = evidenceList.map { it.toDomain() },
    evidenceSummary = evidenceSummary,
    reasoning = reasoning,
    processingTimeSeconds = processingTimeSeconds,
    sourcesSearched = sourcesSearched
)

// ── Domain → Entity ───────────────────────────────────────────

fun ClaimResult.toEntity() = VerificationEntity(
    id = id,
    claim = claim,
    verdict = verdict.name,
    confidenceScore = confidenceScore,
    evidenceCount = evidenceCount,
    evidenceList = evidenceList.map { it.toEntity() },
    evidenceSummary = evidenceSummary,
    reasoning = reasoning,
    processingTimeSeconds = processingTimeSeconds,
    sourcesSearched = sourcesSearched,
    timestamp = timestamp
)

fun Evidence.toEntity() = EvidenceEntity(
    title = title,
    url = url,
    snippet = snippet,
    relevanceScore = relevanceScore,
    stance = stance,
    sourceCredibility = sourceCredibility,
    sourceType = sourceType
)

// ── Entity → Domain ───────────────────────────────────────────

fun VerificationEntity.toDomain() = ClaimResult(
    id = id,
    claim = claim,
    verdict = Verdict.from(verdict),
    confidenceScore = confidenceScore,
    evidenceCount = evidenceCount,
    evidenceList = evidenceList.map { it.toDomain() },
    evidenceSummary = evidenceSummary,
    reasoning = reasoning,
    processingTimeSeconds = processingTimeSeconds,
    sourcesSearched = sourcesSearched,
    timestamp = timestamp
)

fun EvidenceEntity.toDomain() = Evidence(
    title = title,
    url = url,
    snippet = snippet,
    relevanceScore = relevanceScore,
    stance = stance,
    sourceCredibility = sourceCredibility,
    sourceType = sourceType
)