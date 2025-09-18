package com.sepideh.lilo.core.di

import com.sepideh.lilo.core.di.categoryDatabaseQualifier
import com.sepideh.lilo.core.di.taskDatabaseQualifier
import com.sepideh.lilo.core.service.PermissionManager
import com.sepideh.lilo.database.getCategoryDatabaseBuilder
import com.sepideh.lilo.database.getTaskDatabaseBuilder
import com.sepideh.lilo.task.data.ReminderSchedulerProvider
import com.sepideh.lilo.task.domain.reminder.ReminderScheduler
import org.koin.dsl.module

actual fun platformModule() = module {
    single(taskDatabaseQualifier) { getTaskDatabaseBuilder().build() }
    single(categoryDatabaseQualifier) { getCategoryDatabaseBuilder().build() }
    single<ReminderScheduler> { ReminderSchedulerProvider() }
    single<PermissionManager> { PermissionManager() }

}