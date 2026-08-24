package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CategoryEntity
import com.example.data.model.TaskEntity
import com.example.data.preferences.AppLanguage
import com.example.ui.components.AddEditTaskSheet
import com.example.ui.components.CreateCategoryDialog
import com.example.ui.components.EnjazBottomNavigation
import com.example.ui.components.EnjazFloatingActionButton
import com.example.ui.components.NavDestination
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.AllTasksScreen
import com.example.ui.screens.CalendarScreen
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.CustomizationScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.RegisterScreen
import com.example.ui.screens.RemindersScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.EnjazTheme
import com.example.ui.utils.DateTimeUtils
import com.example.ui.viewmodel.TaskViewModel
import com.example.ui.viewmodel.TaskViewModelFactory

enum class AppScreen {
    SPLASH,
    LOGIN,
    REGISTER,
    MAIN,
    CUSTOMIZATION,
    CATEGORIES,
    ABOUT
}

class MainActivity : ComponentActivity() {

    private val viewModel: TaskViewModel by viewModels {
        val app = application as EnjazApplication
        TaskViewModelFactory(
            taskRepository = app.taskRepository,
            categoryRepository = app.categoryRepository,
            userRepository = app.userRepository,
            userPreferencesRepository = app.userPreferencesRepository,
            reminderScheduler = app.reminderScheduler
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val userPreferences by viewModel.userPreferences.collectAsStateWithLifecycle()
            val categories by viewModel.allCategories.collectAsStateWithLifecycle()

            // Notification permission request on Android 13+
            val notificationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { /* Permission result handled */ }

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            EnjazTheme(preferences = userPreferences) {
                var currentScreen by remember { mutableStateOf(AppScreen.SPLASH) }
                var currentNavDestination by remember { mutableStateOf(NavDestination.HOME) }

                // State for Add/Edit Task Bottom Sheet
                var showAddTaskSheet by remember { mutableStateOf(false) }
                var editingTask by remember { mutableStateOf<TaskEntity?>(null) }
                var taskPreselectedDate by remember { mutableStateOf<Long?>(null) }

                // State for Create Category Dialog
                var showCreateCategoryDialog by remember { mutableStateOf(false) }

                val isArabic = userPreferences.language == AppLanguage.AR

                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "screen_navigation"
                ) { screen ->
                    when (screen) {
                        AppScreen.SPLASH -> {
                            SplashScreen(
                                onFinished = {
                                    currentScreen = if (userPreferences.isLoggedIn) {
                                        AppScreen.MAIN
                                    } else {
                                        AppScreen.LOGIN
                                    }
                                }
                            )
                        }

                        AppScreen.LOGIN -> {
                            LoginScreen(
                                viewModel = viewModel,
                                onNavigateToRegister = { currentScreen = AppScreen.REGISTER },
                                onAuthSuccess = { currentScreen = AppScreen.MAIN },
                                isArabic = isArabic
                            )
                        }

                        AppScreen.REGISTER -> {
                            RegisterScreen(
                                viewModel = viewModel,
                                onNavigateToLogin = { currentScreen = AppScreen.LOGIN },
                                onAuthSuccess = { currentScreen = AppScreen.MAIN },
                                isArabic = isArabic
                            )
                        }

                        AppScreen.MAIN -> {
                            Scaffold(
                                modifier = Modifier.fillMaxSize(),
                                bottomBar = {
                                    EnjazBottomNavigation(
                                        currentDestination = currentNavDestination,
                                        onNavigate = { dest -> currentNavDestination = dest },
                                        onFabClick = {
                                            editingTask = null
                                            taskPreselectedDate = null
                                            showAddTaskSheet = true
                                        },
                                        isArabic = isArabic
                                    )
                                }
                            ) { innerPadding ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding)
                                ) {
                                    // Main screen destinations
                                    when (currentNavDestination) {
                                        NavDestination.HOME -> {
                                            HomeScreen(
                                                viewModel = viewModel,
                                                preferences = userPreferences,
                                                onNavigateToCalendar = { currentNavDestination = NavDestination.CALENDAR },
                                                onNavigateToSettings = { currentNavDestination = NavDestination.SETTINGS },
                                                onNavigateToReminders = { currentNavDestination = NavDestination.REMINDERS },
                                                onEditTask = { task ->
                                                    editingTask = task
                                                    showAddTaskSheet = true
                                                },
                                                isArabic = isArabic
                                            )
                                        }

                                        NavDestination.MY_TASKS -> {
                                            AllTasksScreen(
                                                viewModel = viewModel,
                                                onEditTask = { task ->
                                                    editingTask = task
                                                    showAddTaskSheet = true
                                                },
                                                isArabic = isArabic
                                            )
                                        }

                                        NavDestination.CALENDAR -> {
                                            CalendarScreen(
                                                viewModel = viewModel,
                                                onEditTask = { task ->
                                                    editingTask = task
                                                    showAddTaskSheet = true
                                                },
                                                onAddTaskForDate = { dateMillis ->
                                                    editingTask = null
                                                    taskPreselectedDate = dateMillis
                                                    showAddTaskSheet = true
                                                },
                                                isArabic = isArabic
                                            )
                                        }

                                        NavDestination.REMINDERS -> {
                                            RemindersScreen(
                                                viewModel = viewModel,
                                                onEditTask = { task ->
                                                    editingTask = task
                                                    showAddTaskSheet = true
                                                },
                                                isArabic = isArabic
                                            )
                                        }

                                        NavDestination.SETTINGS -> {
                                            SettingsScreen(
                                                viewModel = viewModel,
                                                preferences = userPreferences,
                                                onNavigateToCustomization = { currentScreen = AppScreen.CUSTOMIZATION },
                                                onNavigateToCategories = { currentScreen = AppScreen.CATEGORIES },
                                                onNavigateToAbout = { currentScreen = AppScreen.ABOUT },
                                                onLogout = { currentScreen = AppScreen.LOGIN },
                                                isArabic = isArabic
                                            )
                                        }
                                    }

                                    // Floating Action Button on Home & Tasks screens
                                    if (currentNavDestination == NavDestination.HOME || currentNavDestination == NavDestination.MY_TASKS) {
                                        EnjazFloatingActionButton(
                                            onClick = {
                                                editingTask = null
                                                taskPreselectedDate = null
                                                showAddTaskSheet = true
                                            },
                                            modifier = Modifier
                                                .align(Alignment.BottomEnd)
                                                .padding(end = 20.dp, bottom = 16.dp)
                                        )
                                    }
                                }
                            }
                        }

                        AppScreen.CUSTOMIZATION -> {
                            CustomizationScreen(
                                viewModel = viewModel,
                                preferences = userPreferences,
                                onBack = { currentScreen = AppScreen.MAIN },
                                isArabic = isArabic
                            )
                        }

                        AppScreen.CATEGORIES -> {
                            CategoriesScreen(
                                viewModel = viewModel,
                                onBack = { currentScreen = AppScreen.MAIN },
                                isArabic = isArabic
                            )
                        }

                        AppScreen.ABOUT -> {
                            AboutScreen(
                                onBack = { currentScreen = AppScreen.MAIN },
                                isArabic = isArabic
                            )
                        }
                    }
                }

                // Add or Edit Task Bottom Sheet Modal
                if (showAddTaskSheet) {
                    val initialTask = editingTask ?: taskPreselectedDate?.let {
                        TaskEntity(
                            title = "",
                            date = it
                        )
                    }

                    AddEditTaskSheet(
                        initialTask = initialTask,
                        categories = categories,
                        onSaveTask = { taskToSave ->
                            if (taskToSave.id == 0L) {
                                viewModel.insertTask(taskToSave)
                            } else {
                                viewModel.updateTask(taskToSave)
                            }
                            showAddTaskSheet = false
                            editingTask = null
                            taskPreselectedDate = null
                        },
                        onDismiss = {
                            showAddTaskSheet = false
                            editingTask = null
                            taskPreselectedDate = null
                        },
                        onOpenCreateCategory = {
                            showCreateCategoryDialog = true
                        },
                        isArabic = isArabic
                    )
                }

                // Create Custom Category Dialog
                if (showCreateCategoryDialog) {
                    CreateCategoryDialog(
                        onDismiss = { showCreateCategoryDialog = false },
                        onSaveCategory = { newCategory ->
                            viewModel.insertCategory(newCategory)
                            showCreateCategoryDialog = false
                        },
                        isArabic = isArabic
                    )
                }
            }
        }
    }
}
