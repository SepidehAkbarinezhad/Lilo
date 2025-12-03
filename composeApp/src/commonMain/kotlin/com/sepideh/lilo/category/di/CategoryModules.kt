package com.sepideh.lilo.category.di

import com.sepideh.lilo.category.domain.factory.CategoryFactory
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import kotlin.coroutines.EmptyCoroutineContext.get

val categoryDatabaseQualifier = named("categoryDatabase")

val categoryModule = module {
single { CategoryFactory(languageProvider = get()) }}

expect fun categoryPlatformModule(): Module

