package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.CategoryEntity
import com.example.data.model.RepeatType
import com.example.data.model.TaskEntity
import com.example.data.model.TaskTimeFilter
import com.example.data.preferences.AppLanguage
import com.example.data.preferences.CardCornerStyle
import com.example.data.preferences.FontScaleSetting
import com.example.data.preferences.PrimaryColorPreset
import com.example.data.preferences.ThemeMode
import com.example.data.preferences.UserPreferences
import com.example.data.preferences.UserPreferencesRepository
import com.example.data.repository.AuthResult
import com.example.data.repository.CategoryRepository
import com.example.data.repository.TaskRepository
import com.example.data.repository.UserRepository
import com.example.reminder.ReminderScheduler
import com.example.ui.utils.DateTimeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository,
    private val userRepository: UserRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val reminderScheduler: ReminderScheduler
) : ViewModel() {

    val allTasks: StateFlow<List<TaskEntity>> = taskRepository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCategories: StateFlow<List<CategoryEntity>> = categoryRepository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userPreferences: StateFlow<UserPreferences> = userPreferencesRepository.userPreferencesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserPreferences())

    private val _lastDeletedTask = MutableStateFlow<TaskEntity?>(null)
    val lastDeletedTask: StateFlow<TaskEntity?> = _lastDeletedTask.asStateFlow()

    init {
        // Ensure default categories are populated on first launch
        viewModelScope.launch {
            categoryRepository.getAllCategoriesDirect()
        }
    }

    fun insertTask(task: TaskEntity) {
        viewModelScope.launch {
            val id = taskRepository.insertTask(task)
            val saved = task.copy(id = id)
            reminderScheduler.scheduleTaskReminder(saved)
        }
    }

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            taskRepository.updateTask(task)
            reminderScheduler.scheduleTaskReminder(task)
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            _lastDeletedTask.value = task
            taskRepository.deleteTask(task)
            reminderScheduler.cancelTaskReminder(task.id)
        }
    }

    fun undoDelete() {
        val task = _lastDeletedTask.value ?: return
        viewModelScope.launch {
            insertTask(task)
            _lastDeletedTask.value = null
        }
    }

    fun toggleTaskComplete(task: TaskEntity, completed: Boolean) {
        viewModelScope.launch {
            taskRepository.toggleTaskCompleted(task.id, completed)
            if (completed) {
                reminderScheduler.cancelTaskReminder(task.id)
            } else {
                reminderScheduler.scheduleTaskReminder(task)
            }
        }
    }

    fun insertCategory(category: CategoryEntity) {
        viewModelScope.launch {
            categoryRepository.insertCategory(category)
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch {
            categoryRepository.deleteCategory(category)
        }
    }

    // Preferences Actions
    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            userPreferencesRepository.setThemeMode(mode)
        }
    }

    fun setColorPreset(preset: PrimaryColorPreset) {
        viewModelScope.launch {
            userPreferencesRepository.setColorPreset(preset)
        }
    }

    fun setCustomColor(colorHex: Long) {
        viewModelScope.launch {
            userPreferencesRepository.setCustomColor(colorHex)
        }
    }

    fun setFontScale(scale: FontScaleSetting) {
        viewModelScope.launch {
            userPreferencesRepository.setFontScale(scale)
        }
    }

    fun setCardStyle(style: CardCornerStyle) {
        viewModelScope.launch {
            userPreferencesRepository.setCardStyle(style)
        }
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            userPreferencesRepository.setLanguage(language)
        }
    }

    // Auth Actions
    suspend fun registerUser(name: String, email: String, pass: String): AuthResult {
        val res = userRepository.register(name, email, pass)
        if (res is AuthResult.Success) {
            userPreferencesRepository.setUserSession(
                isLoggedIn = true,
                userId = res.user.id,
                name = res.user.displayName,
                email = res.user.email
            )
        }
        return res
    }

    suspend fun loginUser(email: String, pass: String): AuthResult {
        val res = userRepository.login(email, pass)
        if (res is AuthResult.Success) {
            userPreferencesRepository.setUserSession(
                isLoggedIn = true,
                userId = res.user.id,
                name = res.user.displayName,
                email = res.user.email
            )
        }
        return res
    }

    fun continueAsGuest() {
        viewModelScope.launch {
            userPreferencesRepository.setUserSession(
                isLoggedIn = true,
                userId = -1L,
                name = "صديق إنجاز",
                email = "guest@enjaz.app"
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPreferencesRepository.logout()
        }
    }
}

class TaskViewModelFactory(
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository,
    private val userRepository: UserRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val reminderScheduler: ReminderScheduler
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            return TaskViewModel(
                taskRepository,
                categoryRepository,
                userRepository,
                userPreferencesRepository,
                reminderScheduler
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
