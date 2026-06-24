package app.dev.policyscanai.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Primary Brand
val PrimaryBlue = Color(0xFF0A4DFF)
val MidnightNavy = Color(0xFF041B4D)

// AI Accent
val AIPurple = Color(0xFF7B2EFF)
val VioletAccent = Color(0xFFA855F7)

// Backgrounds
val PureWhite = Color(0xFFFFFFFF)
val IceGray = Color(0xFFF8FAFC)
val RichBlack = Color(0xFF0B1020)
val DarkSurface = Color(0xFF0F172A)
val DarkCard = Color(0xFF1A2035)
val DarkCardElevated = Color(0xFF1E2845)

// Text
val DarkNavy = Color(0xFF0F172A)
val SlateGray = Color(0xFF64748B)
val MutedBlue = Color(0xFF94A3B8)
val WhiteText = Color(0xFFFFFFFF)

// Risk Status Colors
val RiskRed = Color(0xFFEF4444)
val RiskRedBg = Color(0xFF2D1515)
val RiskOrange = Color(0xFFF59E0B)
val RiskOrangeBg = Color(0xFF2D1F0A)
val RiskYellow = Color(0xFFEAB308)
val RiskYellowBg = Color(0xFF2A2105)
val RiskGreen = Color(0xFF10B981)
val RiskGreenBg = Color(0xFF0A2D1E)

// Divider / Border
val BorderLight = Color(0xFFE2E8F0)
val BorderDark = Color(0xFF1E2D4D)

// Gradient stops (for manual gradient use)
val GradientStart = Color(0xFF0A4DFF)
val GradientMid1 = Color(0xFF2563EB)
val GradientMid2 = Color(0xFF7B2EFF)
val GradientEnd = Color(0xFFA855F7)

/**
 * Custom application colors that are not part of the standard Material 3 ColorScheme.
 * This includes risk status colors and specific gradients.
 */
object AppColors {
    val riskRed = RiskRed
    val riskRedBg = RiskRedBg
    val riskOrange = RiskOrange
    val riskOrangeBg = RiskOrangeBg
    val riskYellow = RiskYellow
    val riskYellowBg = RiskYellowBg
    val riskGreen = RiskGreen
    val riskGreenBg = RiskGreenBg

    /**
     * A 135-degree linear gradient brush using the brand's gradient stops.
     */
    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            GradientStart,
            GradientMid1,
            GradientMid2,
            GradientEnd
        ),
        start = Offset(0f, 0f),
        end = Offset(1000f, 1000f) // Approximating 135 degrees for general use
    )
}
