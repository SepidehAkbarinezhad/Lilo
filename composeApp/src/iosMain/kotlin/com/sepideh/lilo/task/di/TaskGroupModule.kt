package com.sepideh.lilo.task.di

import com.sepideh.lilo.task.data.local.room.getTaskGroupDatabaseBuilder
import org.koin.dsl.module

actual fun taskGroupPlatformModule() = module {
    single(taskGroupDatabaseQualifier) { getTaskGroupDatabaseBuilder().build() }
}