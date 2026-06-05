package eu.tutorials.fact_checker_app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// ── Brand colors ──────────────────────────────────────────────
val PrimaryBlue    = Color(0xFF1A6BCC)
val PrimaryDark    = Color(0xFF4A9EF5)
val TrueGreen      = Color(0xFF2E7D32)
val FalseRed       = Color(0xFFC62828)
val InconclusiveAmber = Color(0xFFE65100)
val NeutralGray    = Color(0xFF546E7A)
val SurfaceLight   = Color(0xFFF8F9FA)
val SurfaceDark    = Color(0xFF1C1B1F)


// ── Dark color scheme ─────────────────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary          = PrimaryDark,
    onPrimary        = Color(0xFF003065),
    primaryContainer = Color(0xFF00468B),
    secondary        = Color(0xFF90A4AE),
    background       = Color(0xFF121212),
    surface          = Color(0xFF1E1E1E),
    surfaceVariant   = Color(0xFF2A2A2A),
    error            = Color(0xFFEF9A9A),
    outline          = Color(0xFF37474F)
)


private val LightColorScheme = lightColorScheme(
    primary          = PrimaryBlue,
    onPrimary        = Color.White,
    primaryContainer = Color(0xFFD6E4FF),
    secondary        = Color(0xFF455A64),
    background       = Color(0xFFFAFAFA),
    surface          = Color.White,
    surfaceVariant   = Color(0xFFF1F3F5),
    error            = FalseRed,
    outline          = Color(0xFFCFD8DC)
)

@Composable
fun Fact_checker_appTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// ── Verdict colors ────────────────────────────────────────────
object VerdictColors {
    val trueBackground    = Color(0xFFE8F5E9)
    val trueContent       = Color(0xFF1B5E20)
    val falseBg           = Color(0xFFFFEBEE)
    val falseContent      = Color(0xFFB71C1C)
    val inconclusiveBg    = Color(0xFFFFF3E0)
    val inconclusiveContent = Color(0xFFE65100)
    val unverifiableBg    = Color(0xFFF5F5F5)
    val unverifiableContent = Color(0xFF424242)

    val trueDark          = Color(0xFF1B5E20)
    val falseDark         = Color(0xFFFFCDD2)
    val inconclusiveDark  = Color(0xFFFFE0B2)
    val unverifiableDark  = Color(0xFF616161)
}
