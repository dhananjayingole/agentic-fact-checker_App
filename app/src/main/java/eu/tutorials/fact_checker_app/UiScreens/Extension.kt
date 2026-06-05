package eu.tutorials.fact_checker_app.UiScreens

import androidx.compose.ui.graphics.Color
import eu.tutorials.fact_checker_app.domain.Verdict
import eu.tutorials.fact_checker_app.ui.theme.VerdictColors
import java.text.SimpleDateFormat
import java.util.*

// ── Verdict helpers ───────────────────────────────────────────

fun Verdict.backgroundColor(isDark: Boolean = false): Color = when (this) {
    Verdict.TRUE          -> if (isDark) VerdictColors.trueDark         else VerdictColors.trueBackground
    Verdict.FALSE         -> if (isDark) VerdictColors.falseDark        else VerdictColors.falseBg
    Verdict.INCONCLUSIVE  -> if (isDark) VerdictColors.inconclusiveDark else VerdictColors.inconclusiveBg
    Verdict.UNVERIFIABLE  -> if (isDark) VerdictColors.unverifiableDark else VerdictColors.unverifiableBg
}

fun Verdict.contentColor(isDark: Boolean = false): Color = when (this) {
    Verdict.TRUE          -> if (isDark) Color.White else VerdictColors.trueContent
    Verdict.FALSE         -> if (isDark) VerdictColors.falseContent else VerdictColors.falseContent
    Verdict.INCONCLUSIVE  -> VerdictColors.inconclusiveContent
    Verdict.UNVERIFIABLE  -> VerdictColors.unverifiableContent
}

fun Verdict.icon(): String = when (this) {
    Verdict.TRUE         -> "✓"
    Verdict.FALSE        -> "✗"
    Verdict.INCONCLUSIVE -> "?"
    Verdict.UNVERIFIABLE -> "—"
}

// ── Confidence helpers ────────────────────────────────────────

fun Double.toConfidenceLabel(): String = when {
    this >= 8.0 -> "Very High"
    this >= 6.0 -> "High"
    this >= 4.0 -> "Medium"
    this >= 2.0 -> "Low"
    else        -> "Very Low"
}

fun Double.toConfidencePercent(): Int = (this * 10).toInt().coerceIn(0, 100)

// ── Timestamp helpers ─────────────────────────────────────────

fun Long.toRelativeTime(): String {
    val diff = System.currentTimeMillis() - this
    val minutes = diff / 60_000
    val hours   = diff / 3_600_000
    val days    = diff / 86_400_000
    return when {
        minutes < 1  -> "Just now"
        minutes < 60 -> "$minutes min ago"
        hours   < 24 -> "$hours hr ago"
        days    < 7  -> "$days days ago"
        else         -> SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(this))
    }
}

fun Long.toFormattedDate(): String =
    SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(this))

// ── String helpers ────────────────────────────────────────────

fun String.truncate(maxLength: Int = 80): String =
    if (length <= maxLength) this else "${take(maxLength)}…"