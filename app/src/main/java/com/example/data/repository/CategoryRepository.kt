package com.example.data.repository

import com.example.data.db.CategoryDao
import com.example.data.model.CategoryEntity
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val categoryDao: CategoryDao) {

    val allCategories: Flow<List<CategoryEntity>> = categoryDao.getAllCategories()

    suspend fun getAllCategoriesDirect(): List<CategoryEntity> {
        val list = categoryDao.getAllCategoriesDirect()
        if (list.isEmpty()) {
            com.example.data.db.AppDatabase.populateDefaultCategories(categoryDao)
            return categoryDao.getAllCategoriesDirect()
        }
        return list
    }

    suspend fun insertCategory(category: CategoryEntity): Long {
        return categoryDao.insertCategory(category)
    }

    suspend fun updateCategory(category: CategoryEntity) {
        categoryDao.updateCategory(category)
    }

    suspend fun deleteCategory(category: CategoryEntity) {
        categoryDao.deleteCategory(category)
    }
}
