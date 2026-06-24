package app.dev.policyscanai.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.dev.policyscanai.R
import app.dev.policyscanai.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    showInitialStateForPreview: Boolean = false
) {
    val isDark = isSystemInDarkTheme()
    
    // Animation states
    var startLogoAnimation by remember { mutableStateOf(showInitialStateForPreview) }
    var startNameAnimation by remember { mutableStateOf(showInitialStateForPreview) }
    var startTaglineAnimation by remember { mutableStateOf(showInitialStateForPreview) }

    val logoScale by animateFloatAsState(
        targetValue = if (startLogoAnimation) 1f else 0.85f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "LogoScale"
    )

    val logoAlpha by animateFloatAsState(
        targetValue = if (startLogoAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 600, easing = EaseOutBack),
        label = "LogoAlpha"
    )

    // Progress bar animation
    val infiniteTransition = rememberInfiniteTransition(label = "ProgressTransition")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ProgressAnimation"
    )

    LaunchedEffect(Unit) {
        if (!showInitialStateForPreview) {
            startLogoAnimation = true
            delay(300)
            startNameAnimation = true
            delay(200) // Delay relative to name start (300+200=500 total)
            startTaglineAnimation = true
            
            delay(2000) // Total 2500ms (500 + 2000)
            onNavigateToHome()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 32.dp)
    ) {
        // Main Content
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo Section
            Box(
                contentAlignment = Alignment.BottomCenter,
                modifier = Modifier.graphicsLayer { alpha = logoAlpha }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(96.dp)
                        .scale(logoScale)
                )
                
                // Gradient Glow
                Box(
                    modifier = Modifier
                        .offset(y = 4.dp) // Overlap logo bottom slightly (-4dp margin top equivalent)
                        .width(120.dp)
                        .height(12.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    PrimaryBlue.copy(alpha = 0.35f),
                                    Color.Transparent
                                )
                            ),
                            shape = RoundedCornerShape(50)
                        )
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // App Name Section
            AnimatedVisibility(
                visible = startNameAnimation,
                enter = fadeIn(animationSpec = tween(500)) + 
                        slideInVertically(
                            initialOffsetY = { 20.dp.value.toInt() },
                            animationSpec = tween(500)
                        )
            ) {
                Text(
                    text = "Policy Scan AI",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp,
                        color = if (isDark) WhiteText else DarkNavy
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tagline Section
            AnimatedVisibility(
                visible = startTaglineAnimation,
                enter = fadeIn(animationSpec = tween(400))
            ) {
                Text(
                    text = "SCAN. DETECT. PROTECT.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 1.5.sp,
                        color = SlateGray
                    )
                )
            }
        }

        // Bottom Loading Indicator
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 56.dp)
                .width(120.dp)
                .height(2.dp)
                .clip(RoundedCornerShape(50))
                .background(Color.Transparent)
        ) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxSize(),
                color = if (isDark) PrimaryBlue else AIPurple,
                trackColor = Color.Transparent,
                drawStopIndicator = {}
            )
        }
    }
}
