package com.sepideh.lilo.task.di

import com.sepideh.lilo.task.data.local.room.TaskDatabase
import com.sepideh.lilo.task.data.local.room.getTaskDatabaseBuilder
import org.koin.dsl.module

actual fun taskPlatformModule()= module {
    single(taskDatabaseQualifier) { getTaskDatabaseBuilder(ctx = get()).build()}
    single { get<TaskDatabase>(taskDatabaseQualifier).taskDao() }

}

