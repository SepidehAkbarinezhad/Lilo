package com.sepideh.lilo.task.di

import com.sepideh.lilo.task.data.local.room.getTaskDatabaseBuilder
import org.koin.dsl.module

actual fun taskPlatformModule() = module {
    single(taskDatabaseQualifier) { getTaskDatabaseBuilder().build() }
}