package com.sepideh.lilo.core.di

import com.sepideh.lilo.core.data.local.dataStore.createDataStore
import org.koin.core.module.Module
import org.koin.dsl.module


expect fun corePlatformModule(): Module

val coreModule = module {
    single {
        createDataStore { "user_preferences.preferences_pb" }
    }
}