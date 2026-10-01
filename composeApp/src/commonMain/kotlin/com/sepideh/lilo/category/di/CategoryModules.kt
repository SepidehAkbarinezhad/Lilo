package com.sepideh.lilo.category.di
import com.sepideh.lilo.category.data.local.room.CategoryDatabase
import com.sepideh.lilo.category.data.reposirotyImpl.CategoryRepositoryImpl
import com.sepideh.lilo.category.domain.CategoryFactory
import com.sepideh.lilo.category.domain.repository.CategoryRepository
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

val categoryDatabaseQualifier = named("categoryDatabase")

val categoryModule = module {
    single { CategoryFactory(languageProvider = get()) }
    single { get<CategoryDatabase>(categoryDatabaseQualifier).categoryDao() }
    single<CategoryRepository> { CategoryRepositoryImpl(categoryDao = get()) }

}

expect fun categoryPlatformModule(): Module

