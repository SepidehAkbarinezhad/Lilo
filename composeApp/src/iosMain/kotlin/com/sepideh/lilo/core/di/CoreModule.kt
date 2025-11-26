package com.sepideh.lilo.core.di

import com.sepideh.lilo.core.data.local.dataStore.createDataStoreIOS
import com.sepideh.lilo.core.service.PermissionManager
import com.sepideh.lilo.task.data.ReminderSchedulerProvider
import com.sepideh.lilo.task.data.local.room.getTaskDatabaseBuilder
import com.sepideh.lilo.task.di.taskDatabaseQualifier
import com.sepideh.lilo.task.domain.reminder.ReminderScheduler
import org.koin.dsl.module

actual fun corePlatformModule() = module {
    single<ReminderScheduler> { ReminderSchedulerProvider() }
    single<PermissionManager> { PermissionManager() }
    single { createDataStoreIOS() }
}