package eu.tutorials.fact_checker_app.domain

enum class Verdict(val label: String, val emoji: String) {
    TRUE("True", "✓"),
    FALSE("False", "✗"),
    INCONCLUSIVE("Inconclusive", "?"),
    UNVERIFIABLE("Unverifiable", "—");

    companion object {
        fun from(raw: String): Verdict = when (raw.uppercase()) {
            "TRUE"         -> TRUE
            "FALSE"        -> FALSE
            "INCONCLUSIVE" -> INCONCLUSIVE
            else           -> UNVERIFIABLE
        }
    }
}

//Evidence items
data class Evidence(
    val title: String,
    val url: String,
    val snippet: String,
    val relevanceScore: Double,
    val stance: String,          // "supports" | "contradicts" | "neutral"
    val sourceCredibility: String, // "high" | "medium" | "low"
    val sourceType: String        // "web" | "wikipedia"
)

//main result model
data class ClaimResult(
    val id: Long = 0L,
    val claim: String,
    val verdict: Verdict,
    val confidenceScore: Double,
    val evidenceCount: Int,
    val evidenceList: List<Evidence>,
    val evidenceSummary: String,
    val reasoning: String,
    val processingTimeSeconds: Double,
    val sourcesSearched: Int,
    val timestamp: Long = System.currentTimeMillis()
)

// ── UI state wrapper ──────────────────────────────────────────
sealed class VerifyState {
    object Idle : VerifyState()
    object Loading : VerifyState()
    data class Success(val result: ClaimResult) : VerifyState()
    data class Error(val message: String) : VerifyState()
}

// ── Stats model (for home screen) ────────────────────────────
data class CheckStats(
    val total: Int = 0,
    val trueCount: Int = 0,
    val falseCount: Int = 0,
    val inconclusiveCount: Int = 0
) {
    val truePercent: Int get() = if (total == 0) 0 else (trueCount * 100 / total)
    val falsePercent: Int get() = if (total == 0) 0 else (falseCount * 100 / total)
}

