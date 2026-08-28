package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.UserEntity

@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserByIdFlow(id: Long): kotlinx.coroutines.flow.Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @androidx.room.Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET passwordHash = :passwordHash, salt = :salt WHERE id = :id")
    suspend fun updatePassword(id: Long, passwordHash: String, salt: String)

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int
}
