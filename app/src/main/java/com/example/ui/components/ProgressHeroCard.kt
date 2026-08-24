package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.localization.LocalAppStrings
import com.example.ui.theme.getPrimaryGradient

@Composable
fun ProgressHeroCard(
    totalTasks: Int,
    completedTasks: Int,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val progress = if (totalTasks > 0) completedTasks.toFloat() / totalTasks.toFloat() else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 800),
        label = "hero_progress_anim"
    )

    val percentage = (progress * 100).toInt()

    val motivationalText = when {
        totalTasks == 0 -> strings.motivationalStartDay
        completedTasks == totalTasks -> strings.motivationalCompletedAll
        progress >= 0.75f -> strings.motivationalAlmostDone
        progress >= 0.4f -> strings.motivationalGreat
        completedTasks > 0 -> strings.motivationalKeepGoing
        else -> strings.motivationalStartDay
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(32.dp),
                ambientColor = Color.Black.copy(alpha = 0.3f),
                spotColor = primaryColor.copy(alpha = 0.25f)
            )
            .testTag("progress_hero_card"),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF151B2D)
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                listOf(
                    Color.White.copy(alpha = 0.08f),
                    Color.White.copy(alpha = 0.03f)
                )
            ),
            width = 1.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF151B2D),
                            Color(0xFF1C2438)
                        )
                    )
                )
                .padding(horizontal = 24.dp, vertical = 22.dp)
        ) {
            // Subtle glowing gradient accent in background
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .align(Alignment.BottomEnd)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                primaryColor.copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left Column: Texts and Stats
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = strings.todayAchievement,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "$percentage% ${strings.completedStatus}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "${String.format(strings.tasksCompletedOf, completedTasks, totalTasks)}. $motivationalText",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF9CA3AF),
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Right: Circular Progress Ring with Percentage
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(80.dp)
                        .testTag("progress_ring")
                ) {
                    // Background track
                    CircularProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier.size(80.dp),
                        color = Color.White.copy(alpha = 0.05f),
                        strokeWidth = 7.dp,
                        strokeCap = StrokeCap.Round
                    )

                    // Active progress
                    CircularProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.size(80.dp),
                        color = primaryColor,
                        strokeWidth = 7.dp,
                        strokeCap = StrokeCap.Round
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$percentage%",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
