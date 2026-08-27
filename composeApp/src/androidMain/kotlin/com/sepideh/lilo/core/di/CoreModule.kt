package com.sepideh.lilo.core.di

import com.sepideh.lilo.core.data.local.dataStore.createDataStoreAndroid
import com.sepideh.lilo.core.service.PermissionManager
import com.sepideh.lilo.core.utils.LanguageManager
import com.sepideh.lilo.task.data.ReminderSchedulerProvider
import com.sepideh.lilo.task.domain.reminder.ReminderScheduler
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual fun corePlatformModule() = module {
    single<ReminderScheduler> { ReminderSchedulerProvider(get()) }
    single<PermissionManager> { PermissionManager(get()) }
    single { createDataStoreAndroid(androidContext()) }
    single { LanguageManager(context = androidContext()) }

}

