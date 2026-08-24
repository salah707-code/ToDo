package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CategoryEntity
import com.example.data.model.TaskEntity
import com.example.data.model.TaskStatusFilter
import com.example.ui.components.CategoryIcons
import com.example.ui.components.TaskCard
import com.example.ui.localization.LocalAppStrings
import com.example.ui.utils.DateTimeUtils
import com.example.ui.viewmodel.TaskViewModel
import kotlinx.coroutines.launch

@Composable
fun AllTasksScreen(
    viewModel: TaskViewModel,
    onEditTask: (TaskEntity) -> Unit,
    modifier: Modifier = Modifier,
    isArabic: Boolean = true
) {
    val strings = LocalAppStrings.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val allTasks by viewModel.allTasks.collectAsStateWithLifecycle()
    val categories by viewModel.allCategories.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf(TaskStatusFilter.ALL) }
    var selectedCategoryFilter by remember { mutableStateOf<String?>(null) }

    val primaryColor = MaterialTheme.colorScheme.primary

    // Filtered list
    val filteredTasks by remember(allTasks, searchQuery, selectedStatus, selectedCategoryFilter) {
        derivedStateOf {
            val todayStart = DateTimeUtils.getTodayStartMillis()

            allTasks.filter { task ->
                // Search query match
                val matchesQuery = searchQuery.isBlank() ||
                        task.title.contains(searchQuery, ignoreCase = true) ||
                        task.description.contains(searchQuery, ignoreCase = true) ||
                        task.category.contains(searchQuery, ignoreCase = true)

                // Category match
                val matchesCategory = selectedCategoryFilter == null || task.category == selectedCategoryFilter

                // Status match
                val matchesStatus = when (selectedStatus) {
                    TaskStatusFilter.ALL -> true
                    TaskStatusFilter.PENDING -> !task.isCompleted
                    TaskStatusFilter.COMPLETED -> task.isCompleted
                    TaskStatusFilter.UPCOMING -> !task.isCompleted && DateTimeUtils.normalizeToStartOfDay(task.date) >= todayStart
                }

                matchesQuery && matchesCategory && matchesStatus
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("all_tasks_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Header
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = strings.navMyTasks,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Search Field
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text(strings.searchTasks) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = primaryColor)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = null)
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("tasks_search_input"),
                        shape = MaterialTheme.shapes.medium,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }

            // Status Filter Tabs (الكل, غير المكتملة, المكتملة, القادمة)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TaskStatusFilter.values().forEach { status ->
                        val isSelected = status == selectedStatus
                        val title = if (isArabic) status.titleAr else status.titleEn

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (isSelected) primaryColor else MaterialTheme.colorScheme.surface
                                )
                                .clickable { selectedStatus = status }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                .testTag("status_filter_${status.name.lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Category Filter Chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // "All Categories" chip
                    val isAllSelected = selectedCategoryFilter == null
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isAllSelected) primaryColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { selectedCategoryFilter = null }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = strings.allStatus,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isAllSelected) primaryColor else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    categories.forEach { cat ->
                        val catName = if (isArabic) cat.nameAr else cat.nameEn
                        val isSelected = selectedCategoryFilter == cat.nameAr || selectedCategoryFilter == cat.nameEn
                        val catColor = Color(cat.colorHex)

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) catColor.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable {
                                    selectedCategoryFilter = if (isSelected) null else cat.nameAr
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(CategoryIcons.getEmoji(cat.iconName), fontSize = 12.sp)
                                Text(
                                    text = catName,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) catColor else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // Results count
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${filteredTasks.size} ${if (isArabic) "مهمة موجودة" else "tasks found"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Task List
            if (filteredTasks.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(primaryColor.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Outlined.Inbox,
                                    contentDescription = null,
                                    tint = primaryColor,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (searchQuery.isNotEmpty()) "لا توجد نتائج مطابقة" else strings.noTasksToday,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
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
                                    viewModel.deleteTask(task)
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
}
