package com.example.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.data.model.ReminderType
import com.example.data.model.TaskEntity
import java.util.Calendar
import java.util.concurrent.TimeUnit

class ReminderScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val workManager = WorkManager.getInstance(context)

    fun scheduleTaskReminder(task: TaskEntity) {
        if (task.isCompleted || task.reminderType == ReminderType.NONE) {
            cancelTaskReminder(task.id)
            return
        }

        val triggerTime = calculateTriggerTime(task)
        val now = System.currentTimeMillis()
        if (triggerTime <= now) {
            // In the past, don't schedule
            return
        }

        val delayMillis = triggerTime - now

        // 1. Schedule via WorkManager (Reliable background execution)
        val inputData = Data.Builder()
            .putLong(TaskReminderWorker.KEY_TASK_ID, task.id)
            .putString(TaskReminderWorker.KEY_TASK_TITLE, task.title)
            .putString(TaskReminderWorker.KEY_TASK_CATEGORY, task.category)
            .putLong(TaskReminderWorker.KEY_TASK_COLOR, task.categoryColor)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<TaskReminderWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag("${TaskReminderWorker.WORK_TAG_PREFIX}${task.id}")
            .build()

        workManager.enqueueUniqueWork(
            "${TaskReminderWorker.WORK_TAG_PREFIX}${task.id}",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )

        // 2. Dual Schedule via AlarmManager (Immediate RTC wakeup)
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_TASK_ID, task.id)
            putExtra(AlarmReceiver.EXTRA_TASK_TITLE, task.title)
            putExtra(AlarmReceiver.EXTRA_TASK_CATEGORY, task.category)
            putExtra(AlarmReceiver.EXTRA_TASK_COLOR, task.categoryColor)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }

    fun cancelTaskReminder(taskId: Long) {
        // Cancel WorkManager job
        try {
            workManager.cancelUniqueWork("${TaskReminderWorker.WORK_TAG_PREFIX}$taskId")
            workManager.cancelAllWorkByTag("${TaskReminderWorker.WORK_TAG_PREFIX}$taskId")
        } catch (e: Exception) {
            Log.e("ReminderScheduler", "Error canceling WorkManager: ${e.message}")
        }

        // Cancel AlarmManager alarm
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
        }
    }

    companion object {
        fun calculateTriggerTime(task: TaskEntity): Long {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = task.date
                if (task.timeHour in 0..23 && task.timeMinute in 0..59) {
                    set(Calendar.HOUR_OF_DAY, task.timeHour)
                    set(Calendar.MINUTE, task.timeMinute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                } else {
                    set(Calendar.HOUR_OF_DAY, 9)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
            }

            val taskMillis = calendar.timeInMillis
            val offsetMillis = task.reminderType.minutesBefore * 60 * 1000L
            return taskMillis - offsetMillis
        }
    }
}
