package com.sepideh.lilo.di

import com.sepideh.lilo.database.getCategoryDatabaseBuilder
import com.sepideh.lilo.database.getTaskDatabaseBuilder
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual fun platformModule() = module {
    single(taskDatabaseQualifier) { getTaskDatabaseBuilder().build() }
    single(categoryDatabaseQualifier) { getCategoryDatabaseBuilder().build() }
}