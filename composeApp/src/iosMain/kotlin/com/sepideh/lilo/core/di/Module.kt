package com.sepideh.lilo.core.di

import com.sepideh.lilo.category.data.local.room.getCategoryDatabaseBuilder
import com.sepideh.lilo.core.service.PermissionManager
import com.sepideh.lilo.task.data.ReminderSchedulerProvider
import com.sepideh.lilo.task.data.local.room.getTaskDatabaseBuilder
import com.sepideh.lilo.task.domain.reminder.ReminderScheduler
import org.koin.dsl.module

actual fun platformModule() = module {
    single(taskDatabaseQualifier) { getTaskDatabaseBuilder().build() }
    single(categoryDatabaseQualifier) { getCategoryDatabaseBuilder().build() }
    single<ReminderScheduler> { ReminderSchedulerProvider() }
    single<PermissionManager> { PermissionManager() }

}