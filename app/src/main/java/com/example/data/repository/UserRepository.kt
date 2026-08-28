package com.example.data.repository

import com.example.data.db.UserDao
import com.example.data.model.UserEntity
import com.example.security.PasswordSecurity

sealed class AuthResult {
    data class Success(val user: UserEntity) : AuthResult()
    data class Error(val messageAr: String, val messageEn: String) : AuthResult()
}

class UserRepository(private val userDao: UserDao) {

    suspend fun register(name: String, email: String, password: String): AuthResult {
        val trimmedEmail = email.trim().lowercase()
        val trimmedName = name.trim()

        if (trimmedName.isBlank()) {
            return AuthResult.Error("يرجى إدخال الاسم", "Please enter your name")
        }
        if (trimmedEmail.isBlank() || !trimmedEmail.contains("@")) {
            return AuthResult.Error("يرجى إدخال بريد إلكتروني صالح", "Please enter a valid email")
        }
        if (password.length < 6) {
            return AuthResult.Error("كلمة المرور يجب أن تكون 6 أحرف على الأقل", "Password must be at least 6 characters")
        }

        val existing = userDao.getUserByEmail(trimmedEmail)
        if (existing != null) {
            return AuthResult.Error("البريد الإلكتروني مسجل مسبقاً", "Email already registered")
        }

        val salt = PasswordSecurity.generateSalt()
        val hash = PasswordSecurity.hashPassword(password, salt)
        val newUser = UserEntity(
            displayName = trimmedName,
            email = trimmedEmail,
            passwordHash = hash,
            salt = salt
        )
        val id = userDao.insertUser(newUser)
        return AuthResult.Success(newUser.copy(id = id))
    }

    suspend fun login(email: String, password: String): AuthResult {
        val trimmedEmail = email.trim().lowercase()
        if (trimmedEmail.isBlank() || password.isBlank()) {
            return AuthResult.Error("يرجى ملء جميع الحقول", "Please fill all fields")
        }

        val user = userDao.getUserByEmail(trimmedEmail)
            ?: return AuthResult.Error("البريد الإلكتروني أو كلمة المرور غير صحيحة", "Invalid email or password")

        val isValid = PasswordSecurity.verifyPassword(password, user.salt, user.passwordHash)
        return if (isValid) {
            AuthResult.Success(user)
        } else {
            AuthResult.Error("البريد الإلكتروني أو كلمة المرور غير صحيحة", "Invalid email or password")
        }
    }

    suspend fun getUserById(id: Long): UserEntity? {
        return userDao.getUserById(id)
    }

    fun getUserByIdFlow(id: Long): kotlinx.coroutines.flow.Flow<UserEntity?> {
        return userDao.getUserByIdFlow(id)
    }

    suspend fun updateProfile(
        userId: Long,
        name: String,
        phone: String,
        address: String,
        jobTitle: String,
        avatarIndex: Int,
        avatarColor: Long
    ): Boolean {
        val existing = userDao.getUserById(userId) ?: return false
        val updated = existing.copy(
            displayName = name.trim(),
            phoneNumber = phone.trim(),
            address = address.trim(),
            jobTitle = jobTitle.trim(),
            avatarIndex = avatarIndex,
            avatarColor = avatarColor
        )
        userDao.updateUser(updated)
        return true
    }

    suspend fun changePassword(userId: Long, oldPass: String, newPass: String): AuthResult {
        if (newPass.length < 6) {
            return AuthResult.Error("كلمة المرور الجديدة يجب أن تكون 6 أحرف على الأقل", "New password must be at least 6 characters")
        }
        val user = userDao.getUserById(userId)
            ?: return AuthResult.Error("المستخدم غير موجود", "User not found")

        val isOldValid = PasswordSecurity.verifyPassword(oldPass, user.salt, user.passwordHash)
        if (!isOldValid) {
            return AuthResult.Error("كلمة المرور الحالية غير صحيحة", "Current password is incorrect")
        }

        val newSalt = PasswordSecurity.generateSalt()
        val newHash = PasswordSecurity.hashPassword(newPass, newSalt)
        userDao.updatePassword(userId, newHash, newSalt)
        return AuthResult.Success(user.copy(passwordHash = newHash, salt = newSalt))
    }
}
