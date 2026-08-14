package com.sepideh.lilo.category.data.reposirotyImpl

import com.sepideh.lilo.category.data.local.room.CategoryDao
import com.sepideh.lilo.category.data.local.room.toDomain
import com.sepideh.lilo.category.data.local.room.toDomainList
import com.sepideh.lilo.category.data.local.room.toEntity
import com.sepideh.lilo.category.domain.CategoryDomain
import com.sepideh.lilo.category.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao,
) : CategoryRepository {
    override fun getAllCategories(): Flow<List<CategoryDomain>> =
        categoryDao.getAllCategories()
            .onEach { if (it.isEmpty()) upsertDefaultCategories() }
            .map { it.toDomainList() }

    private suspend fun upsertDefaultCategories() {
        CategoryDomain.categories
            .subList(1, CategoryDomain.categories.size)
            .forEach { item ->
                categoryDao.upsert(item.toEntity())
            }
    }

    override suspend fun addCategory(category: CategoryDomain) {
        categoryDao.upsert(category.toEntity())
    }

    override suspend fun deleteCategory(id: Long) {
        categoryDao.deleteById(id)
    }

    override suspend fun getCategoryById(id: Long): CategoryDomain? {
        return categoryDao.getCategoryById(id)?.toDomain()
    }
}