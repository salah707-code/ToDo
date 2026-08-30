package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.CategoryEntity
import com.example.data.model.Priority
import com.example.data.model.RepeatType
import com.example.data.model.TaskEntity
import com.example.data.model.TaskSortOrder
import com.example.data.model.UserEntity
import com.example.data.preferences.AppLanguage
import com.example.data.preferences.CardBorderStyle
import com.example.data.preferences.CardCornerStyle
import com.example.data.preferences.CardLayoutStyle
import com.example.data.preferences.CardShadowStyle
import com.example.data.preferences.FontFamilySetting
import com.example.data.preferences.FontScaleSetting
import com.example.data.preferences.IconThemeStyle
import com.example.data.preferences.NotificationTone
import com.example.data.preferences.PrimaryColorPreset
import com.example.data.preferences.ThemeMode
import com.example.data.preferences.UserPreferences
import com.example.data.preferences.UserPreferencesRepository
import com.example.data.repository.AuthResult
import com.example.data.repository.CategoryRepository
import com.example.data.repository.TaskRepository
import com.example.data.repository.UserRepository
import com.example.reminder.ReminderScheduler
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

    val trashTasks: StateFlow<List<TaskEntity>> = taskRepository.trashTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val trashCount: StateFlow<Int> = taskRepository.trashCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val allCategories: StateFlow<List<CategoryEntity>> = categoryRepository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userPreferences: StateFlow<UserPreferences> = userPreferencesRepository.userPreferencesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserPreferences())

    private val _sortOrder = MutableStateFlow(TaskSortOrder.DATE_TIME)
    val sortOrder: StateFlow<TaskSortOrder> = _sortOrder.asStateFlow()

    private val _lastDeletedTask = MutableStateFlow<TaskEntity?>(null)
    val lastDeletedTask: StateFlow<TaskEntity?> = _lastDeletedTask.asStateFlow()

    init {
        // Ensure default categories are populated on first launch
        viewModelScope.launch {
            categoryRepository.getAllCategoriesDirect()
        }
    }

    fun setSortOrder(order: TaskSortOrder) {
        _sortOrder.value = order
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
            taskRepository.moveToTrash(task.id)
            reminderScheduler.cancelTaskReminder(task.id)
        }
    }

    fun restoreFromTrash(task: TaskEntity) {
        viewModelScope.launch {
            taskRepository.restoreFromTrash(task.id)
            if (!task.isCompleted && task.reminderType != com.example.data.model.ReminderType.NONE) {
                reminderScheduler.scheduleTaskReminder(task)
            }
        }
    }

    fun permanentlyDeleteTask(task: TaskEntity) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
            reminderScheduler.cancelTaskReminder(task.id)
        }
    }

    fun emptyTrash() {
        viewModelScope.launch {
            taskRepository.emptyTrash()
        }
    }

    fun undoDelete() {
        val task = _lastDeletedTask.value ?: return
        viewModelScope.launch {
            taskRepository.restoreFromTrash(task.id)
            if (!task.isCompleted && task.reminderType != com.example.data.model.ReminderType.NONE) {
                reminderScheduler.scheduleTaskReminder(task)
            }
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

    fun setFontFamily(family: FontFamilySetting) {
        viewModelScope.launch {
            userPreferencesRepository.setFontFamily(family)
        }
    }

    fun setCardStyle(style: CardCornerStyle) {
        viewModelScope.launch {
            userPreferencesRepository.setCardStyle(style)
        }
    }

    fun setCardLayoutStyle(layout: CardLayoutStyle) {
        viewModelScope.launch {
            userPreferencesRepository.setCardLayoutStyle(layout)
        }
    }

    fun setCardShadowStyle(shadow: CardShadowStyle) {
        viewModelScope.launch {
            userPreferencesRepository.setCardShadowStyle(shadow)
        }
    }

    fun setCardBorderStyle(border: CardBorderStyle) {
        viewModelScope.launch {
            userPreferencesRepository.setCardBorderStyle(border)
        }
    }

    fun setNotificationTone(tone: NotificationTone) {
        viewModelScope.launch {
            userPreferencesRepository.setNotificationTone(tone)
        }
    }

    fun setNotificationVolume(volume: Float) {
        viewModelScope.launch {
            userPreferencesRepository.setNotificationVolume(volume)
        }
    }

    fun setIconThemeStyle(style: IconThemeStyle) {
        viewModelScope.launch {
            userPreferencesRepository.setIconThemeStyle(style)
        }
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            userPreferencesRepository.setLanguage(language)
        }
    }

    // Auth & Profile Actions
    suspend fun registerUser(name: String, email: String, pass: String): AuthResult {
        val res = userRepository.register(name, email, pass)
        if (res is AuthResult.Success) {
            userPreferencesRepository.setUserSession(
                isLoggedIn = true,
                userId = res.user.id,
                name = res.user.displayName,
                email = res.user.email,
                phone = res.user.phoneNumber,
                address = res.user.address,
                jobTitle = res.user.jobTitle,
                avatarIndex = res.user.avatarIndex,
                avatarColor = res.user.avatarColor
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
                email = res.user.email,
                phone = res.user.phoneNumber,
                address = res.user.address,
                jobTitle = res.user.jobTitle,
                avatarIndex = res.user.avatarIndex,
                avatarColor = res.user.avatarColor
            )
        }
        return res
    }

    suspend fun updateUserProfile(
        name: String,
        phone: String,
        address: String,
        jobTitle: String,
        avatarIndex: Int,
        avatarColor: Long
    ): Boolean {
        val userId = userPreferences.value.userId
        if (userId > 0) {
            userRepository.updateProfile(userId, name, phone, address, jobTitle, avatarIndex, avatarColor)
        }
        userPreferencesRepository.updateProfileCache(name, phone, address, jobTitle, avatarIndex, avatarColor)
        return true
    }

    suspend fun changePassword(oldPass: String, newPass: String): AuthResult {
        val userId = userPreferences.value.userId
        if (userId <= 0) {
            return AuthResult.Error("يرجى تسجيل الدخول أولاً لتغيير كلمة المرور", "Please sign in to change password")
        }
        return userRepository.changePassword(userId, oldPass, newPass)
    }

    fun continueAsGuest() {
        viewModelScope.launch {
            userPreferencesRepository.setUserSession(
                isLoggedIn = true,
                userId = -1L,
                name = "صديق إنجاز",
                email = "guest@enjaz.app",
                phone = "",
                address = "",
                jobTitle = "عضو مميز",
                avatarIndex = 0,
                avatarColor = 0xFF4F46E5
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
