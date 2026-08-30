package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.TaskEntity
import com.example.data.model.TaskSortOrder
import com.example.data.model.TaskTimeFilter
import com.example.data.preferences.UserPreferences
import com.example.ui.components.FilterChipsRow
import com.example.ui.components.ProgressHeroCard
import com.example.ui.components.TaskCard
import com.example.ui.components.TrashSheet
import com.example.ui.localization.LocalAppStrings
import com.example.ui.utils.DateTimeUtils
import com.example.ui.viewmodel.TaskViewModel
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun HomeScreen(
    viewModel: TaskViewModel,
    preferences: UserPreferences,
    onNavigateToCalendar: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToReminders: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onEditTask: (TaskEntity) -> Unit,
    modifier: Modifier = Modifier,
    isArabic: Boolean = true
) {
    val strings = LocalAppStrings.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val allTasks by viewModel.allTasks.collectAsStateWithLifecycle()
    val trashTasks by viewModel.trashTasks.collectAsStateWithLifecycle()
    val sortOrder by viewModel.sortOrder.collectAsStateWithLifecycle()

    var selectedFilter by remember { mutableStateOf(TaskTimeFilter.TODAY) }
    var showTrashSheet by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }
    var taskToDeleteConfirm by remember { mutableStateOf<TaskEntity?>(null) }

    val todayStart = remember { DateTimeUtils.getTodayStartMillis() }
    val tomorrowStart = remember { DateTimeUtils.getTomorrowStartMillis() }
    val weekEnd = remember { DateTimeUtils.getThisWeekEndMillis() }

    // Filter tasks based on selected tab
    val filteredTasks by remember(allTasks, selectedFilter) {
        derivedStateOf {
            when (selectedFilter) {
                TaskTimeFilter.TODAY -> allTasks.filter {
                    DateTimeUtils.normalizeToStartOfDay(it.date) == todayStart
                }
                TaskTimeFilter.TOMORROW -> allTasks.filter {
                    DateTimeUtils.normalizeToStartOfDay(it.date) == tomorrowStart
                }
                TaskTimeFilter.THIS_WEEK -> allTasks.filter {
                    val d = DateTimeUtils.normalizeToStartOfDay(it.date)
                    d in todayStart..weekEnd
                }
                TaskTimeFilter.LATER -> allTasks.filter {
                    val d = DateTimeUtils.normalizeToStartOfDay(it.date)
                    d > weekEnd
                }
                TaskTimeFilter.ALL -> allTasks
            }
        }
    }

    // Today's total and completed count for the Hero card
    val todayTasks = remember(allTasks) {
        allTasks.filter { DateTimeUtils.normalizeToStartOfDay(it.date) == todayStart }
    }
    val todayTotalCount = todayTasks.size
    val todayCompletedCount = todayTasks.count { it.isCompleted }

    // Counts map for chips
    val countsMap = remember(allTasks) {
        mapOf(
            TaskTimeFilter.TODAY to allTasks.count { DateTimeUtils.normalizeToStartOfDay(it.date) == todayStart },
            TaskTimeFilter.TOMORROW to allTasks.count { DateTimeUtils.normalizeToStartOfDay(it.date) == tomorrowStart },
            TaskTimeFilter.THIS_WEEK to allTasks.count {
                val d = DateTimeUtils.normalizeToStartOfDay(it.date)
                d in todayStart..weekEnd
            },
            TaskTimeFilter.LATER to allTasks.count {
                val d = DateTimeUtils.normalizeToStartOfDay(it.date)
                d > weekEnd
            },
            TaskTimeFilter.ALL to allTasks.size
        )
    }

    // Greeting according to time of day
    val currentHour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    val greeting = when {
        currentHour in 4..11 -> strings.greetingMorning
        currentHour in 12..16 -> strings.greetingAfternoon
        else -> strings.greetingEvening
    }
    val userName = if (preferences.userName.isNotBlank()) preferences.userName else strings.guestUser

    val primaryColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("home_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Top Bar Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Date on top, User Profile Avatar + Greeting with emoji below
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // User Profile Avatar Clickable Button
                            val avatarColor = Color(preferences.userAvatarColor)
                            val avatarEmoji = AvatarEmojis.getOrElse(preferences.userAvatarIndex) { "👤" }
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(avatarColor.copy(alpha = 0.25f))
                                    .border(1.8.dp, avatarColor, CircleShape)
                                    .clickable { onNavigateToProfile() }
                                    .testTag("home_profile_avatar"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = avatarEmoji, fontSize = 20.sp)
                            }

                            Column {
                                Text(
                                    text = DateTimeUtils.formatFullDate(System.currentTimeMillis(), isArabic),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Normal
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "$greeting ",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = userName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = primaryColor
                                    )
                                }
                            }
                        }

                        // Header Action Icons with border & sleek background
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Trash Button with Badge
                            IconButton(
                                onClick = { showTrashSheet = true },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surface)
                                    .shadow(elevation = 2.dp, shape = CircleShape)
                                    .testTag("home_trash_button")
                            ) {
                                BadgedBox(
                                    badge = {
                                        if (trashTasks.isNotEmpty()) {
                                            Badge {
                                                Text(trashTasks.size.toString())
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteSweep,
                                        contentDescription = strings.trash,
                                        tint = if (trashTasks.isNotEmpty()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            IconButton(
                                onClick = onNavigateToCalendar,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surface)
                                    .shadow(elevation = 2.dp, shape = CircleShape)
                                    .testTag("home_calendar_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = strings.navCalendar,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            IconButton(
                                onClick = onNavigateToReminders,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surface)
                                    .shadow(elevation = 2.dp, shape = CircleShape)
                                    .testTag("home_reminders_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = strings.navReminders,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            IconButton(
                                onClick = onNavigateToSettings,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surface)
                                    .shadow(elevation = 2.dp, shape = CircleShape)
                                    .testTag("home_settings_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = strings.navSettings,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Big "إنجاز اليوم" Card
            item {
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                    ProgressHeroCard(
                        totalTasks = todayTotalCount,
                        completedTasks = todayCompletedCount
                    )
                }
            }

            // Filter Chips (اليوم, غداً, هذا الأسبوع, لاحقاً)
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    FilterChipsRow(
                        selectedFilter = selectedFilter,
                        onFilterSelected = { selectedFilter = it },
                        countsMap = countsMap,
                        isArabic = isArabic
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Section Title with Sort Options Dropdown
            item {
                val listHeader = when (selectedFilter) {
                    TaskTimeFilter.TODAY -> "مهام اليوم"
                    TaskTimeFilter.TOMORROW -> "مهام غداً"
                    TaskTimeFilter.THIS_WEEK -> "مهام هذا الأسبوع"
                    TaskTimeFilter.LATER -> "مهام لاحقاً"
                    TaskTimeFilter.ALL -> "جميع المهام"
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (isArabic) listHeader else selectedFilter.titleEn,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "(${filteredTasks.size})",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Sort Button & Dropdown Menu
                    Box {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .clickable { showSortMenu = true }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sort,
                                contentDescription = strings.sortBy,
                                tint = primaryColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = when (sortOrder) {
                                    TaskSortOrder.PRIORITY_HIGH_FIRST -> strings.priorityHigh
                                    TaskSortOrder.PRIORITY_LOW_FIRST -> strings.priorityLow
                                    TaskSortOrder.DATE_TIME -> strings.sortByDateTime
                                    TaskSortOrder.ALPHABETICAL -> strings.sortByAlpha
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = primaryColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {
                            DropdownMenuItem(
                                text = { Text(strings.sortByPriority + " (الأعلى أولاً)") },
                                onClick = {
                                    viewModel.setSortOrder(TaskSortOrder.PRIORITY_HIGH_FIRST)
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(strings.sortByPriority + " (الأقل أولاً)") },
                                onClick = {
                                    viewModel.setSortOrder(TaskSortOrder.PRIORITY_LOW_FIRST)
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(strings.sortByDateTime) },
                                onClick = {
                                    viewModel.setSortOrder(TaskSortOrder.DATE_TIME)
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(strings.sortByAlpha) },
                                onClick = {
                                    viewModel.setSortOrder(TaskSortOrder.ALPHABETICAL)
                                    showSortMenu = false
                                }
                            )
                        }
                    }
                }
            }

            // Task list items or empty state
            if (filteredTasks.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(primaryColor.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.CheckCircle,
                                    contentDescription = null,
                                    tint = primaryColor,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = strings.noTasksToday,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = strings.noTasksDescription,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 24.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(
                    items = filteredTasks,
                    key = { it.id }
                ) { task ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(tween(250)) + slideInVertically(tween(250)),
                        exit = fadeOut(tween(200)) + slideOutVertically(tween(200))
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 20.dp, vertical = 6.dp)
                                .animateItem()
                        ) {
                            TaskCard(
                                task = task,
                                onToggleComplete = { isComplete ->
                                    viewModel.toggleTaskComplete(task, isComplete)
                                },
                                onEdit = { onEditTask(task) },
                                onDelete = {
                                    // Trigger delete confirmation dialog
                                    taskToDeleteConfirm = task
                                },
                                layoutStyle = preferences.cardLayoutStyle,
                                shadowStyle = preferences.cardShadowStyle,
                                borderStyle = preferences.cardBorderStyle,
                                iconThemeStyle = preferences.iconThemeStyle,
                                cornerStyle = preferences.cardStyle,
                                isArabic = isArabic
                            )
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp, start = 16.dp, end = 16.dp)
        )
    }

    // Delete Confirmation Dialog (تنبيه عند الحذف)
    taskToDeleteConfirm?.let { task ->
        AlertDialog(
            onDismissRequest = { taskToDeleteConfirm = null },
            title = { Text(strings.confirmDeleteTitle) },
            text = { Text(strings.confirmDeleteMessage) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteTask(task)
                        taskToDeleteConfirm = null
                        coroutineScope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = strings.taskDeleted,
                                actionLabel = strings.undo,
                                duration = SnackbarDuration.Short
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                viewModel.undoDelete()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(strings.delete, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { taskToDeleteConfirm = null }) {
                    Text(strings.cancel)
                }
            }
        )
    }

    // Trash Bottom Sheet (سلة المهملات)
    if (showTrashSheet) {
        TrashSheet(
            trashTasks = trashTasks,
            onRestoreTask = { viewModel.restoreFromTrash(it) },
            onPermanentlyDeleteTask = { viewModel.permanentlyDeleteTask(it) },
            onEmptyTrash = { viewModel.emptyTrash() },
            onDismiss = { showTrashSheet = false },
            isArabic = isArabic
        )
    }
}

