package app.dev.policyscanai.ui.components

import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.dev.policyscanai.ui.screens.LiveFinding

@Composable
fun LiveFindingChip(
    finding: LiveFinding,
    isNew: Boolean
) {
    val isDark = isSystemInDarkTheme()
    
    val bgColor = when (finding.riskLevel) {
        "RED" -> if (isDark) Color(0xFF2D1515) else Color(0xFFFFEEEE)
        "ORANGE" -> if (isDark) Color(0xFF2D1F0A) else Color(0xFFFFF3E0)
        else -> if (isDark) Color(0xFF2A2105) else Color(0xFFFFFDE7)
    }
    
    val badgeColor = when (finding.riskLevel) {
        "RED" -> Color(0xFFEF4444)
        "ORANGE" -> Color(0xFFF59E0B)
        else -> Color(0xFFEAB308)
    }

    val slideIn by animateFloatAsState(
        targetValue = if (isNew) 1f else 1f, // Standardizing alpha to 1 for simplicity or implement real animation
        animationSpec = tween(400, easing = EaseOutCubic),
        label = "FindingSlideIn"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { alpha = slideIn },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Risk Badge
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = badgeColor
            ) {
                Text(
                    text = finding.riskLevel,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    color = Color.White
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = finding.title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Page ${finding.pageNumber}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isNew) {
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = Color(0xFF0A4DFF).copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "NEW",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = Color(0xFF0A4DFF)
                    )
                }
            }
        }
    }
}
