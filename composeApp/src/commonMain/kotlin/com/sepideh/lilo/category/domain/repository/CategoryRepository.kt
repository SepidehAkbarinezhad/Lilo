package com.sepideh.lilo.category.domain.repository

import com.sepideh.lilo.category.domain.CategoryDomain
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getAllCategories(): Flow<List<CategoryDomain>>
    suspend fun addCategory(category: CategoryDomain)
    suspend fun deleteCategory(id: Long)
    suspend fun getCategoryById(id: Long): CategoryDomain?
}