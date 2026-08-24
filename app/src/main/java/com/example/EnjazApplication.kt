package com.example

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.data.db.AppDatabase
import com.example.data.preferences.UserPreferencesRepository
import com.example.data.repository.CategoryRepository
import com.example.data.repository.TaskRepository
import com.example.data.repository.UserRepository
import com.example.reminder.ReminderScheduler

class EnjazApplication : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var taskRepository: TaskRepository
        private set

    lateinit var categoryRepository: CategoryRepository
        private set

    lateinit var userRepository: UserRepository
        private set

    lateinit var userPreferencesRepository: UserPreferencesRepository
        private set

    lateinit var reminderScheduler: ReminderScheduler
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        database = AppDatabase.getInstance(this)
        taskRepository = TaskRepository(database.taskDao())
        categoryRepository = CategoryRepository(database.categoryDao())
        userRepository = UserRepository(database.userDao())
        userPreferencesRepository = UserPreferencesRepository(this)
        reminderScheduler = ReminderScheduler(this)

        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_REMINDERS,
                "تذكيرات المهام (Task Reminders)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "إشعارات وتذكيرات المهام اليومية المجدولة"
                enableVibration(true)
                enableLights(true)
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_REMINDERS = "enjaz_task_reminders_channel"
        lateinit var instance: EnjazApplication
            private set
    }
}
