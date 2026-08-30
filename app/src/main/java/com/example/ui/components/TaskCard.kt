package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Priority
import com.example.data.model.ReminderType
import com.example.data.model.RepeatType
import com.example.data.model.TaskEntity
import com.example.data.preferences.CardBorderStyle
import com.example.data.preferences.CardCornerStyle
import com.example.data.preferences.CardLayoutStyle
import com.example.data.preferences.CardShadowStyle
import com.example.data.preferences.IconThemeStyle
import com.example.ui.localization.LocalAppStrings
import com.example.ui.theme.SuccessGreen
import com.example.ui.utils.DateTimeUtils

@Composable
fun TaskCard(
    task: TaskEntity,
    onToggleComplete: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    layoutStyle: CardLayoutStyle = CardLayoutStyle.STANDARD,
    shadowStyle: CardShadowStyle = CardShadowStyle.MEDIUM,
    borderStyle: CardBorderStyle = CardBorderStyle.SUBTLE_LINE,
    iconThemeStyle: IconThemeStyle = IconThemeStyle.COLORED_EMOJI,
    cornerStyle: CardCornerStyle = CardCornerStyle.ROUNDED,
    isArabic: Boolean = true
) {
    when (layoutStyle) {
        CardLayoutStyle.LARGE_GRID -> {
            TaskCardLargeGrid(
                task = task,
                onToggleComplete = onToggleComplete,
                onEdit = onEdit,
                onDelete = onDelete,
                modifier = modifier,
                shadowStyle = shadowStyle,
                borderStyle = borderStyle,
                iconThemeStyle = iconThemeStyle,
                cornerStyle = cornerStyle,
                isArabic = isArabic
            )
        }
        CardLayoutStyle.COMPACT_LIST -> {
            TaskCardCompactList(
                task = task,
                onToggleComplete = onToggleComplete,
                onEdit = onEdit,
                onDelete = onDelete,
                modifier = modifier,
                shadowStyle = shadowStyle,
                borderStyle = borderStyle,
                iconThemeStyle = iconThemeStyle,
                cornerStyle = cornerStyle,
                isArabic = isArabic
            )
        }
        CardLayoutStyle.STANDARD -> {
            TaskCardStandard(
                task = task,
                onToggleComplete = onToggleComplete,
                onEdit = onEdit,
                onDelete = onDelete,
                modifier = modifier,
                shadowStyle = shadowStyle,
                borderStyle = borderStyle,
                iconThemeStyle = iconThemeStyle,
                cornerStyle = cornerStyle,
                isArabic = isArabic
            )
        }
    }
}

// 1. STANDARD CARD
@Composable
fun TaskCardStandard(
    task: TaskEntity,
    onToggleComplete: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    shadowStyle: CardShadowStyle = CardShadowStyle.MEDIUM,
    borderStyle: CardBorderStyle = CardBorderStyle.SUBTLE_LINE,
    iconThemeStyle: IconThemeStyle = IconThemeStyle.COLORED_EMOJI,
    cornerStyle: CardCornerStyle = CardCornerStyle.ROUNDED,
    isArabic: Boolean = true
) {
    val strings = LocalAppStrings.current
    var showMenu by remember { mutableStateOf(false) }

    // Use individual task card color if present, else category color
    val effectiveColorHex = if (task.cardColorHex != 0L) task.cardColorHex else task.categoryColor
    val cardAccentColor = Color(effectiveColorHex)
    val priorityColor = Color(task.priority.colorHex)

    val surfaceBg = if (task.isCompleted) {
        Color(0xFF151B2D).copy(alpha = 0.6f)
    } else {
        if (task.cardColorHex != 0L) {
            Color(0xFF151B2D).copy(alpha = 0.92f)
        } else {
            Color(0xFF151B2D)
        }
    }

    val cardShape = RoundedCornerShape(cornerStyle.cornerRadiusDp.dp)

    val cardBorderModifier = when (borderStyle) {
        CardBorderStyle.NONE -> Modifier
        CardBorderStyle.SUBTLE_LINE -> Modifier.border(0.8.dp, Color(0xFF2E3856), cardShape)
        CardBorderStyle.COLORED_BORDER -> Modifier.border(1.5.dp, cardAccentColor.copy(alpha = 0.7f), cardShape)
        CardBorderStyle.GLOW_BORDER -> Modifier.border(
            width = 2.dp,
            brush = Brush.linearGradient(listOf(cardAccentColor, cardAccentColor.copy(alpha = 0.3f))),
            shape = cardShape
        )
    }

    val shadowElevation = shadowStyle.elevationDp.dp

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = shadowElevation, shape = cardShape, spotColor = cardAccentColor.copy(alpha = 0.35f))
            .then(cardBorderModifier)
            .clickable { onEdit() }
            .testTag("task_card_${task.id}"),
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = surfaceBg)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Category Chip & Priority & Location
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Category Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(cardAccentColor.copy(alpha = 0.18f))
                            .border(1.dp, cardAccentColor.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (iconThemeStyle == IconThemeStyle.COLORED_EMOJI) {
                                Text(
                                    text = CategoryIcons.getEmoji(task.categoryIcon),
                                    fontSize = 12.sp
                                )
                            } else {
                                Icon(
                                    imageVector = CategoryIcons.getIcon(task.categoryIcon),
                                    contentDescription = null,
                                    tint = cardAccentColor,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                            Text(
                                text = task.category,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = cardAccentColor
                            )
                        }
                    }

                    // Priority Flag Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(priorityColor.copy(alpha = 0.16f))
                            .border(1.dp, priorityColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = "Priority",
                                tint = priorityColor,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = if (isArabic) task.priority.titleAr else task.priority.titleEn,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = priorityColor,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Location Badge if enabled
                    if (task.reminderByLocation && task.locationName.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF06B6D4).copy(alpha = 0.16f))
                                .border(1.dp, Color(0xFF06B6D4).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 7.dp, vertical = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Location",
                                    tint = Color(0xFF06B6D4),
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = task.locationName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF06B6D4),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    if (task.repeatType != RepeatType.NONE) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF1E293B))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Repeat,
                                contentDescription = "Repeating",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }

                    if (task.reminderType != ReminderType.NONE) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF1E293B))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "Reminder Active",
                                tint = cardAccentColor,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }

                // Options Menu
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(28.dp).testTag("task_menu_${task.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(Color(0xFF1E2438))
                    ) {
                        DropdownMenuItem(
                            text = { Text(strings.openCardDetails, color = Color(0xFFF9FAFB)) },
                            onClick = {
                                showMenu = false
                                onEdit()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF6366F1))
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(strings.delete, color = Color(0xFFEF4444)) },
                            onClick = {
                                showMenu = false
                                onDelete()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = Color(0xFFEF4444))
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Middle Row: Checkbox + Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Interactive Checkbox
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(if (task.isCompleted) SuccessGreen else Color.Transparent)
                        .border(
                            width = 2.dp,
                            color = if (task.isCompleted) SuccessGreen else Color(0xFF475569),
                            shape = CircleShape
                        )
                        .clickable { onToggleComplete(!task.isCompleted) },
                    contentAlignment = Alignment.Center
                ) {
                    if (task.isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Title & Notes/Description
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onEdit() }
                ) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (task.isCompleted) Color(0xFF94A3B8) else Color(0xFFF9FAFB),
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (task.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8),
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Bottom Row: Date & Time Info
            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(14.dp)
                    )

                    val dateText = DateTimeUtils.formatDateDisplay(task.date, isArabic)
                    val timeText = if (task.timeHour in 0..23 && task.timeMinute in 0..59) {
                        " • ${DateTimeUtils.formatTimeDisplay(task.timeHour, task.timeMinute, isArabic)}"
                    } else ""

                    Text(
                        text = "$dateText$timeText",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8)
                    )
                }

                if (task.isCompleted && task.completedAt != null) {
                    Text(
                        text = if (isArabic) "مكتملة ✓" else "Done ✓",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreen
                    )
                }
            }
        }
    }
}

// 2. LARGE GRID SQUARE CARD (مربعات كبيرة)
@Composable
fun TaskCardLargeGrid(
    task: TaskEntity,
    onToggleComplete: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    shadowStyle: CardShadowStyle = CardShadowStyle.MEDIUM,
    borderStyle: CardBorderStyle = CardBorderStyle.SUBTLE_LINE,
    iconThemeStyle: IconThemeStyle = IconThemeStyle.COLORED_EMOJI,
    cornerStyle: CardCornerStyle = CardCornerStyle.ROUNDED,
    isArabic: Boolean = true
) {
    val strings = LocalAppStrings.current
    var showMenu by remember { mutableStateOf(false) }

    val effectiveColorHex = if (task.cardColorHex != 0L) task.cardColorHex else task.categoryColor
    val cardAccentColor = Color(effectiveColorHex)
    val priorityColor = Color(task.priority.colorHex)

    val surfaceBg = if (task.isCompleted) {
        Color(0xFF151B2D).copy(alpha = 0.55f)
    } else {
        Color(0xFF151B2D)
    }

    val cardShape = RoundedCornerShape(cornerStyle.cornerRadiusDp.dp)

    val cardBorderModifier = when (borderStyle) {
        CardBorderStyle.NONE -> Modifier
        CardBorderStyle.SUBTLE_LINE -> Modifier.border(0.8.dp, Color(0xFF2E3856), cardShape)
        CardBorderStyle.COLORED_BORDER -> Modifier.border(1.5.dp, cardAccentColor.copy(alpha = 0.75f), cardShape)
        CardBorderStyle.GLOW_BORDER -> Modifier.border(
            width = 2.dp,
            brush = Brush.linearGradient(listOf(cardAccentColor, cardAccentColor.copy(alpha = 0.3f))),
            shape = cardShape
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = shadowStyle.elevationDp.dp, shape = cardShape, spotColor = cardAccentColor.copy(alpha = 0.4f))
            .then(cardBorderModifier)
            .clickable { onEdit() }
            .testTag("task_card_grid_${task.id}"),
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = surfaceBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Tag & Priority Tag
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(cardAccentColor.copy(alpha = 0.2f))
                            .border(1.dp, cardAccentColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (iconThemeStyle == IconThemeStyle.COLORED_EMOJI) {
                                Text(CategoryIcons.getEmoji(task.categoryIcon), fontSize = 13.sp)
                            } else {
                                Icon(
                                    imageVector = CategoryIcons.getIcon(task.categoryIcon),
                                    contentDescription = null,
                                    tint = cardAccentColor,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                            Text(
                                text = task.category,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = cardAccentColor
                            )
                        }
                    }

                    // Priority Flag Chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(priorityColor.copy(alpha = 0.16f))
                            .border(1.dp, priorityColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = "Priority",
                            tint = priorityColor,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                // Checkbox Button
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(if (task.isCompleted) SuccessGreen else Color(0xFF1E293B))
                        .border(
                            width = 2.dp,
                            color = if (task.isCompleted) SuccessGreen else Color(0xFF475569),
                            shape = CircleShape
                        )
                        .clickable { onToggleComplete(!task.isCompleted) },
                    contentAlignment = Alignment.Center
                ) {
                    if (task.isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Done",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Large Title
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (task.isCompleted) Color(0xFF94A3B8) else Color(0xFFF9FAFB),
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            if (task.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF94A3B8),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bottom Time & Edit Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val dateStr = DateTimeUtils.formatDateDisplay(task.date, isArabic)
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF94A3B8)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF94A3B8), modifier = Modifier.size(15.dp))
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(15.dp))
                    }
                }
            }
        }
    }
}

// 3. COMPACT LIST ROW (قائمة سريعة ومضغوطة)
@Composable
fun TaskCardCompactList(
    task: TaskEntity,
    onToggleComplete: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    shadowStyle: CardShadowStyle = CardShadowStyle.SUBTLE,
    borderStyle: CardBorderStyle = CardBorderStyle.SUBTLE_LINE,
    iconThemeStyle: IconThemeStyle = IconThemeStyle.COLORED_EMOJI,
    cornerStyle: CardCornerStyle = CardCornerStyle.MEDIUM,
    isArabic: Boolean = true
) {
    val effectiveColorHex = if (task.cardColorHex != 0L) task.cardColorHex else task.categoryColor
    val cardAccentColor = Color(effectiveColorHex)
    val priorityColor = Color(task.priority.colorHex)

    val surfaceBg = if (task.isCompleted) Color(0xFF151B2D).copy(alpha = 0.5f) else Color(0xFF151B2D)
    val cardShape = RoundedCornerShape(cornerStyle.cornerRadiusDp.dp)

    val cardBorderModifier = when (borderStyle) {
        CardBorderStyle.NONE -> Modifier
        CardBorderStyle.SUBTLE_LINE -> Modifier.border(0.8.dp, Color(0xFF2E3856), cardShape)
        CardBorderStyle.COLORED_BORDER -> Modifier.border(1.2.dp, cardAccentColor.copy(alpha = 0.6f), cardShape)
        CardBorderStyle.GLOW_BORDER -> Modifier.border(1.5.dp, cardAccentColor, cardShape)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = shadowStyle.elevationDp.dp, shape = cardShape)
            .then(cardBorderModifier)
            .clickable { onEdit() }
            .testTag("task_card_compact_${task.id}"),
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = surfaceBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(if (task.isCompleted) SuccessGreen else Color.Transparent)
                    .border(
                        width = 1.8.dp,
                        color = if (task.isCompleted) SuccessGreen else Color(0xFF475569),
                        shape = CircleShape
                    )
                    .clickable { onToggleComplete(!task.isCompleted) },
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Done",
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Color Pip Indicator
            Box(
                modifier = Modifier
                    .size(width = 4.dp, height = 24.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(cardAccentColor)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Priority Dot
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(priorityColor)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Title
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onEdit() }
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (task.isCompleted) Color(0xFF94A3B8) else Color(0xFFF9FAFB),
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${task.category} • ${DateTimeUtils.formatDateDisplay(task.date, isArabic)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF94A3B8),
                    maxLines = 1
                )
            }

            // Quick Delete Button
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete",
                    tint = Color(0xFFEF4444).copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
