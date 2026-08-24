package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.EventBusy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.TaskEntity
import com.example.ui.components.TaskCard
import com.example.ui.localization.LocalAppStrings
import com.example.ui.utils.DateTimeUtils
import com.example.ui.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class CalendarViewMode {
    MONTH,
    WEEK,
    DAY
}

data class CalendarDay(
    val dayOfMonth: Int,
    val dateMillis: Long,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val isSelected: Boolean,
    val taskCount: Int
)

@Composable
fun CalendarScreen(
    viewModel: TaskViewModel,
    onEditTask: (TaskEntity) -> Unit,
    onAddTaskForDate: (Long) -> Unit,
    modifier: Modifier = Modifier,
    isArabic: Boolean = true
) {
    val strings = LocalAppStrings.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val allTasks by viewModel.allTasks.collectAsStateWithLifecycle()

    var viewMode by remember { mutableStateOf(CalendarViewMode.MONTH) }
    var displayedCalendar by remember { mutableStateOf(Calendar.getInstance()) }
    var selectedDateMillis by remember { mutableStateOf(DateTimeUtils.getTodayStartMillis()) }

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    // Calculate calendar days
    val calendarDays by remember(displayedCalendar, allTasks, selectedDateMillis) {
        derivedStateOf {
            val days = mutableListOf<CalendarDay>()
            val cal = displayedCalendar.clone() as Calendar

            cal.set(Calendar.DAY_OF_MONTH, 1)
            val month = cal.get(Calendar.MONTH)
            val year = cal.get(Calendar.YEAR)

            val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // 1 = Sunday, 7 = Saturday
            // In Arabic/Middle East, week starts Saturday (7). Let's calculate offset
            val startOffset = (firstDayOfWeek + 1) % 7

            val prevMonthCal = cal.clone() as Calendar
            prevMonthCal.add(Calendar.MONTH, -1)
            val maxPrevDays = prevMonthCal.getActualMaximum(Calendar.DAY_OF_MONTH)

            // Previous month padding
            for (i in startOffset - 1 downTo 0) {
                val d = maxPrevDays - i
                prevMonthCal.set(Calendar.DAY_OF_MONTH, d)
                val millis = DateTimeUtils.normalizeToStartOfDay(prevMonthCal.timeInMillis)
                val count = allTasks.count { DateTimeUtils.normalizeToStartOfDay(it.date) == millis }
                days.add(
                    CalendarDay(
                        dayOfMonth = d,
                        dateMillis = millis,
                        isCurrentMonth = false,
                        isToday = millis == DateTimeUtils.getTodayStartMillis(),
                        isSelected = millis == selectedDateMillis,
                        taskCount = count
                    )
                )
            }

            // Current month days
            val maxCurrentDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
            for (d in 1..maxCurrentDays) {
                cal.set(Calendar.DAY_OF_MONTH, d)
                val millis = DateTimeUtils.normalizeToStartOfDay(cal.timeInMillis)
                val count = allTasks.count { DateTimeUtils.normalizeToStartOfDay(it.date) == millis }
                days.add(
                    CalendarDay(
                        dayOfMonth = d,
                        dateMillis = millis,
                        isCurrentMonth = true,
                        isToday = millis == DateTimeUtils.getTodayStartMillis(),
                        isSelected = millis == selectedDateMillis,
                        taskCount = count
                    )
                )
            }

            // Next month padding to fill complete grid (up to 35 or 42 cells)
            val remaining = (7 - (days.size % 7)) % 7
            val nextMonthCal = cal.clone() as Calendar
            nextMonthCal.add(Calendar.MONTH, 1)
            for (d in 1..remaining) {
                nextMonthCal.set(Calendar.DAY_OF_MONTH, d)
                val millis = DateTimeUtils.normalizeToStartOfDay(nextMonthCal.timeInMillis)
                val count = allTasks.count { DateTimeUtils.normalizeToStartOfDay(it.date) == millis }
                days.add(
                    CalendarDay(
                        dayOfMonth = d,
                        dateMillis = millis,
                        isCurrentMonth = false,
                        isToday = millis == DateTimeUtils.getTodayStartMillis(),
                        isSelected = millis == selectedDateMillis,
                        taskCount = count
                    )
                )
            }

            days
        }
    }

    // Tasks for selected date
    val selectedDateTasks by remember(allTasks, selectedDateMillis) {
        derivedStateOf {
            allTasks.filter {
                DateTimeUtils.normalizeToStartOfDay(it.date) == selectedDateMillis
            }
        }
    }

    // Month name title
    val monthYearTitle = remember(displayedCalendar, isArabic) {
        val locale = if (isArabic) Locale("ar") else Locale.ENGLISH
        val formatter = SimpleDateFormat("MMMM yyyy", locale)
        formatter.format(displayedCalendar.time)
    }

    val dayHeaders = if (isArabic) {
        listOf("س", "ح", "ن", "ث", "ر", "خ", "ج")
    } else {
        listOf("Sa", "Su", "Mo", "Tu", "We", "Th", "Fr")
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("calendar_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Header & Month Navigator
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = strings.navCalendar,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Calendar Card Container
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 8.dp,
                                shape = MaterialTheme.shapes.medium,
                                ambientColor = Color.Black.copy(alpha = 0.05f)
                            ),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Month Title & Arrows
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = monthYearTitle,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(
                                        onClick = {
                                            val c = displayedCalendar.clone() as Calendar
                                            c.add(Calendar.MONTH, -1)
                                            displayedCalendar = c
                                        },
                                        modifier = Modifier.size(32.dp).testTag("prev_month_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Previous Month",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            val c = displayedCalendar.clone() as Calendar
                                            c.add(Calendar.MONTH, 1)
                                            displayedCalendar = c
                                        },
                                        modifier = Modifier.size(32.dp).testTag("next_month_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = "Next Month",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Day of Week Headers
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                dayHeaders.forEach { header ->
                                    Text(
                                        text = header,
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Calendar Days Grid
                            val rows = calendarDays.chunked(7)
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                rows.forEach { weekRow ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        weekRow.forEach { day ->
                                            val isSelected = day.isSelected
                                            val isToday = day.isToday

                                            val bgColor = when {
                                                isSelected -> primaryColor
                                                isToday -> primaryColor.copy(alpha = 0.12f)
                                                else -> Color.Transparent
                                            }

                                            val textColor = when {
                                                isSelected -> Color.White
                                                !day.isCurrentMonth -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                                                isToday -> primaryColor
                                                else -> MaterialTheme.colorScheme.onSurface
                                            }

                                            Column(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .aspectRatio(1f)
                                                    .clip(CircleShape)
                                                    .background(bgColor)
                                                    .clickable {
                                                        selectedDateMillis = day.dateMillis
                                                    }
                                                    .testTag("cal_day_${day.dayOfMonth}"),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Text(
                                                    text = day.dayOfMonth.toString(),
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                                    color = textColor
                                                )

                                                // Task indicator dot
                                                if (day.taskCount > 0) {
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Box(
                                                        modifier = Modifier
                                                            .size(4.dp)
                                                            .clip(CircleShape)
                                                            .background(
                                                                if (isSelected) Color.White else primaryColor
                                                            )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Selected Date Tasks Section Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = DateTimeUtils.formatDateDisplay(selectedDateMillis, isArabic),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${selectedDateTasks.size} ${if (isArabic) "مهام في هذا اليوم" else "tasks for this day"}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = { onAddTaskForDate(selectedDateMillis) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("add_task_for_date_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Text(text = strings.addTask, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Tasks List for selected day
            if (selectedDateTasks.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(primaryColor.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Outlined.EventBusy,
                                    contentDescription = null,
                                    tint = primaryColor,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = strings.noTasksToday,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(
                    items = selectedDateTasks,
                    key = { it.id }
                ) { task ->
                    Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                        TaskCard(
                            task = task,
                            onToggleComplete = { isComplete ->
                                viewModel.toggleTaskComplete(task, isComplete)
                            },
                            onEdit = { onEditTask(task) },
                            onDelete = { viewModel.deleteTask(task) },
                            isArabic = isArabic
                        )
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 80.dp)
        )
    }
}
