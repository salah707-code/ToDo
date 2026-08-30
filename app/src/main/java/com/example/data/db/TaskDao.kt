package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks WHERE isDeleted = 0 ORDER BY isCompleted ASC, date ASC, timeHour ASC, timeMinute ASC, id DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isDeleted = 0 AND date >= :startDate AND date <= :endDate ORDER BY isCompleted ASC, timeHour ASC, timeMinute ASC, id DESC")
    fun getTasksByDateRange(startDate: Long, endDate: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    fun getTaskById(id: Long): Flow<TaskEntity?>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskByIdDirect(id: Long): TaskEntity?

    @Query("SELECT * FROM tasks WHERE isDeleted = 0 AND isCompleted = 0 AND reminderType != 'NONE' ORDER BY date ASC, timeHour ASC, timeMinute ASC")
    fun getPendingRemindersFlow(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isDeleted = 0 AND isCompleted = 0 AND reminderType != 'NONE'")
    suspend fun getPendingRemindersDirect(): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE isDeleted = 1 ORDER BY deletedAt DESC, id DESC")
    fun getTrashTasks(): Flow<List<TaskEntity>>

    @Query("SELECT COUNT(*) FROM tasks WHERE isDeleted = 1")
    fun getTrashCount(): Flow<Int>

    @Query("UPDATE tasks SET isDeleted = 1, deletedAt = :deletedAt WHERE id = :id")
    suspend fun moveToTrash(id: Long, deletedAt: Long = System.currentTimeMillis())

    @Query("UPDATE tasks SET isDeleted = 0, deletedAt = NULL WHERE id = :id")
    suspend fun restoreFromTrash(id: Long)

    @Query("DELETE FROM tasks WHERE isDeleted = 1")
    suspend fun emptyTrash()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Long)

    @Query("UPDATE tasks SET isCompleted = :isCompleted, completedAt = :completedAt, updatedAt = :updatedAt WHERE id = :id")
    suspend fun setTaskCompleted(id: Long, isCompleted: Boolean, completedAt: Long?, updatedAt: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM tasks WHERE isDeleted = 0")
    fun getTotalTasksCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE isDeleted = 0 AND isCompleted = 1")
    fun getCompletedTasksCount(): Flow<Int>
}
