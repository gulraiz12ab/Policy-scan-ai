package app.dev.policyscanai.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = PureWhite,
    primaryContainer = Color(0xFFDBE8FF),
    onPrimaryContainer = MidnightNavy,
    secondary = AIPurple,
    onSecondary = PureWhite,
    secondaryContainer = Color(0xFFEDE0FF),
    onSecondaryContainer = Color(0xFF2D0060),
    tertiary = VioletAccent,
    background = IceGray,
    onBackground = DarkNavy,
    surface = PureWhite,
    onSurface = DarkNavy,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = SlateGray,
    outline = BorderLight,
    error = RiskRed,
    onError = PureWhite
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF6B9FFF),
    onPrimary = MidnightNavy,
    primaryContainer = Color(0xFF0A2B7A),
    onPrimaryContainer = Color(0xFFDBE8FF),
    secondary = Color(0xFFBE94FF),
    onSecondary = Color(0xFF2D0060),
    secondaryContainer = Color(0xFF3D0088),
    onSecondaryContainer = Color(0xFFEDE0FF),
    tertiary = Color(0xFFD4A0FF),
    background = RichBlack,
    onBackground = Color(0xFFE2E8F0),
    surface = DarkSurface,
    onSurface = Color(0xFFE2E8F0),
    surfaceVariant = DarkCard,
    onSurfaceVariant = MutedBlue,
    outline = BorderDark,
    error = Color(0xFFFF6B6B),
    onError = Color(0xFF2D0000)
)

@Composable
fun PolicyScanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
