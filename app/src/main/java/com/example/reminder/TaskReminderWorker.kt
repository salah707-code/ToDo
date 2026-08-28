package com.example.reminder

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.EnjazApplication
import com.example.MainActivity
import com.example.R
import com.example.data.db.AppDatabase
import com.example.data.model.RepeatType
import com.example.data.preferences.NotificationTone
import com.example.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.firstOrNull
import java.util.Calendar

class TaskReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val taskId = inputData.getLong(KEY_TASK_ID, -1L)
        val inputTitle = inputData.getString(KEY_TASK_TITLE) ?: "تذكير بمهمة جديدة"
        val inputCategory = inputData.getString(KEY_TASK_CATEGORY) ?: "مهامي"

        if (taskId == -1L) return Result.failure()

        try {
            val db = AppDatabase.getInstance(context)
            val task = db.taskDao().getTaskByIdDirect(taskId)

            // If task was deleted or marked completed, don't show notification
            if (task != null && task.isCompleted) {
                return Result.success()
            }

            val finalTitle = task?.title ?: inputTitle
            val finalCategory = task?.category ?: inputCategory

            // Retrieve user notification sound preferences
            val prefsRepo = UserPreferencesRepository(context)
            val userPrefs = prefsRepo.userPreferencesFlow.firstOrNull()
            val tone = userPrefs?.notificationTone ?: NotificationTone.CHIME_ALERT
            val volume = userPrefs?.notificationVolume ?: 0.85f

            // Intent to open app directly
            val appIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("OPEN_TASK_ID", taskId)
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                taskId.toInt(),
                appIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Build high priority notification
            val notification = NotificationCompat.Builder(context, EnjazApplication.CHANNEL_REMINDERS)
                .setSmallIcon(R.drawable.app_launcher_icon)
                .setContentTitle(finalTitle)
                .setContentText("⏰ حان موعد: $finalCategory")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setVibrate(longArrayOf(0, 250, 150, 250))
                .setDefaults(if (tone == NotificationTone.SYSTEM_DEFAULT) NotificationCompat.DEFAULT_ALL else NotificationCompat.DEFAULT_LIGHTS or NotificationCompat.DEFAULT_VIBRATE)
                .build()

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(taskId.toInt(), notification)

            // Play custom audio tone separate from system
            if (tone != NotificationTone.SYSTEM_DEFAULT && tone != NotificationTone.MUTE) {
                SoundPlayerUtils.playTone(context, tone, volume)
            }

            // Handle repeat scheduling if repeating
            if (task != null && !task.isCompleted && task.repeatType != RepeatType.NONE) {
                val nextDate = calculateNextRepeatDate(task.date, task.repeatType)
                val updatedTask = task.copy(date = nextDate, updatedAt = System.currentTimeMillis())
                db.taskDao().updateTask(updatedTask)

                // Reschedule for next occurrence
                val scheduler = ReminderScheduler(context)
                scheduler.scheduleTaskReminder(updatedTask)
            }

            return Result.success()
        } catch (e: Exception) {
            Log.e("TaskReminderWorker", "Error executing reminder work: ${e.message}")
            return Result.failure()
        }
    }

    private fun calculateNextRepeatDate(currentDate: Long, repeatType: RepeatType): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = currentDate
        }
        when (repeatType) {
            RepeatType.DAILY -> cal.add(Calendar.DAY_OF_YEAR, 1)
            RepeatType.WEEKLY -> cal.add(Calendar.WEEK_OF_YEAR, 1)
            RepeatType.MONTHLY -> cal.add(Calendar.MONTH, 1)
            else -> cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return cal.timeInMillis
    }

    companion object {
        const val KEY_TASK_ID = "key_task_id"
        const val KEY_TASK_TITLE = "key_task_title"
        const val KEY_TASK_CATEGORY = "key_task_category"
        const val KEY_TASK_COLOR = "key_task_color"
        const val WORK_TAG_PREFIX = "task_reminder_"
    }
}
