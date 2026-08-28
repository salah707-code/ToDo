package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.CategoryEntity
import com.example.data.model.TaskEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [TaskEntity::class, CategoryEntity::class, UserEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun categoryDao(): CategoryDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "enjaz_database"
                )
                    .fallbackToDestructiveMigration(true)
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                populateDefaultCategories(getInstance(context).categoryDao())
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun populateDefaultCategories(categoryDao: CategoryDao) {
            if (categoryDao.getCategoriesCount() == 0) {
                val defaultList = listOf(
                    CategoryEntity(nameAr = "التسوق", nameEn = "Shopping", iconName = "shopping_cart", colorHex = 0xFFF59E0B, sortOrder = 1),
                    CategoryEntity(nameAr = "العمل", nameEn = "Work", iconName = "work", colorHex = 0xFF4F46E5, sortOrder = 2),
                    CategoryEntity(nameAr = "الدراسة", nameEn = "Study", iconName = "school", colorHex = 0xFF8B5CF6, sortOrder = 3),
                    CategoryEntity(nameAr = "المنزل", nameEn = "Home", iconName = "home", colorHex = 0xFF10B981, sortOrder = 4),
                    CategoryEntity(nameAr = "الأدوية", nameEn = "Medicine", iconName = "medication", colorHex = 0xFFEF4444, sortOrder = 5),
                    CategoryEntity(nameAr = "المالية", nameEn = "Finance", iconName = "account_balance_wallet", colorHex = 0xFF059669, sortOrder = 6),
                    CategoryEntity(nameAr = "السيارة", nameEn = "Car", iconName = "directions_car", colorHex = 0xFF3B82F6, sortOrder = 7),
                    CategoryEntity(nameAr = "الرياضة", nameEn = "Sports", iconName = "fitness_center", colorHex = 0xFFEC4899, sortOrder = 8),
                    CategoryEntity(nameAr = "الطعام", nameEn = "Food", iconName = "restaurant", colorHex = 0xFFF97316, sortOrder = 9),
                    CategoryEntity(nameAr = "الاتصالات", nameEn = "Calls", iconName = "call", colorHex = 0xFF06B6D4, sortOrder = 10),
                    CategoryEntity(nameAr = "العائلة", nameEn = "Family", iconName = "family_restroom", colorHex = 0xFF6366F1, sortOrder = 11),
                    CategoryEntity(nameAr = "المواعيد", nameEn = "Appointments", iconName = "event", colorHex = 0xFF14B8A6, sortOrder = 12),
                    CategoryEntity(nameAr = "الصيانة", nameEn = "Maintenance", iconName = "build", colorHex = 0xFF64748B, sortOrder = 13),
                    CategoryEntity(nameAr = "السفر", nameEn = "Travel", iconName = "flight", colorHex = 0xFF0EA5E9, sortOrder = 14),
                    CategoryEntity(nameAr = "شخصي", nameEn = "Personal", iconName = "person", colorHex = 0xFF7C3AED, sortOrder = 15)
                )
                categoryDao.insertAll(defaultList)
            }
        }
    }
}
