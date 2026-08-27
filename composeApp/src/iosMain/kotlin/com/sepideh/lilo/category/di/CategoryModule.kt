package com.sepideh.lilo.category.di

import com.sepideh.lilo.category.data.local.room.getCategoryDatabaseBuilder
import org.koin.dsl.module

actual fun categoryPlatformModule() = module {
    single(categoryDatabaseQualifier) { getCategoryDatabaseBuilder().build() }
}