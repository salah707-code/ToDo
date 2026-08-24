package com.example.data.repository

import com.example.data.db.TaskDao
import com.example.data.model.TaskEntity
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {

    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()
    val pendingReminders: Flow<List<TaskEntity>> = taskDao.getPendingRemindersFlow()

    fun getTasksByDateRange(startDate: Long, endDate: Long): Flow<List<TaskEntity>> {
        return taskDao.getTasksByDateRange(startDate, endDate)
    }

    fun getTaskById(id: Long): Flow<TaskEntity?> {
        return taskDao.getTaskById(id)
    }

    suspend fun getTaskByIdDirect(id: Long): TaskEntity? {
        return taskDao.getTaskByIdDirect(id)
    }

    suspend fun getPendingRemindersDirect(): List<TaskEntity> {
        return taskDao.getPendingRemindersDirect()
    }

    suspend fun insertTask(task: TaskEntity): Long {
        return taskDao.insertTask(task)
    }

    suspend fun updateTask(task: TaskEntity) {
        taskDao.updateTask(task.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteTask(task: TaskEntity) {
        taskDao.deleteTask(task)
    }

    suspend fun deleteTaskById(id: Long) {
        taskDao.deleteTaskById(id)
    }

    suspend fun toggleTaskCompleted(id: Long, completed: Boolean) {
        val completedAt = if (completed) System.currentTimeMillis() else null
        taskDao.setTaskCompleted(id, completed, completedAt)
    }
}
