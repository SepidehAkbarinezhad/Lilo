package com.sepideh.lilo.di

import com.sepideh.lilo.database.getCategoryDatabaseBuilder
import com.sepideh.lilo.database.getTaskDatabaseBuilder
import com.sepideh.lilo.task.data.ReminderManager
import com.sepideh.lilo.task.domain.ReminderScheduler
import org.koin.dsl.module

actual fun platformModule() = module {
    single(taskDatabaseQualifier) { getTaskDatabaseBuilder().build() }
    single(categoryDatabaseQualifier) { getCategoryDatabaseBuilder().build() }
    single<ReminderScheduler> { ReminderManager() }
}