package com.sepideh.lilo.di

import com.sepideh.lilo.database.getCategoryDatabaseBuilder
import com.sepideh.lilo.database.getTaskDatabaseBuilder
import org.koin.dsl.module

actual fun platformModule()= module {
    single(taskDatabaseQualifier) { getTaskDatabaseBuilder(ctx = get()).build()}
    single(categoryDatabaseQualifier) { getCategoryDatabaseBuilder(ctx = get()).build()}
}