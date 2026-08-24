package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskTimeFilter
import com.example.ui.localization.LocalAppStrings

@Composable
fun FilterChipsRow(
    selectedFilter: TaskTimeFilter,
    onFilterSelected: (TaskTimeFilter) -> Unit,
    countsMap: Map<TaskTimeFilter, Int> = emptyMap(),
    modifier: Modifier = Modifier,
    isArabic: Boolean = true
) {
    val filters = listOf(
        TaskTimeFilter.TODAY,
        TaskTimeFilter.TOMORROW,
        TaskTimeFilter.THIS_WEEK,
        TaskTimeFilter.LATER,
        TaskTimeFilter.ALL
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 4.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        filters.forEach { filter ->
            val isSelected = filter == selectedFilter
            val count = countsMap[filter] ?: 0

            val targetBg = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                Color(0xFF151B2D)
            }

            val animatedBg by animateColorAsState(
                targetValue = targetBg,
                animationSpec = tween(durationMillis = 200),
                label = "chip_bg"
            )

            val targetTextColor = if (isSelected) {
                Color.White
            } else {
                Color(0xFF9CA3AF)
            }

            val animatedTextColor by animateColorAsState(
                targetValue = targetTextColor,
                animationSpec = tween(durationMillis = 200),
                label = "chip_text_color"
            )

            val title = if (isArabic) filter.titleAr else filter.titleEn

            Box(
                modifier = Modifier
                    .shadow(
                        elevation = if (isSelected) 8.dp else 0.dp,
                        shape = RoundedCornerShape(24.dp),
                        ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                    )
                    .clip(RoundedCornerShape(24.dp))
                    .background(animatedBg)
                    .clickable { onFilterSelected(filter) }
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .testTag("filter_chip_${filter.name.lowercase()}"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val displayText = if (count > 0) "$title ($count)" else title
                    Text(
                        text = displayText,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        color = animatedTextColor
                    )
                }
            }
        }
    }
}
