package com.sepideh.lilo.di

import com.sepideh.lilo.database.getTaskDatabaseBuilder
import org.koin.dsl.module

val androidModule = module {
    single { getTaskDatabaseBuilder(ctx = get()) }
}