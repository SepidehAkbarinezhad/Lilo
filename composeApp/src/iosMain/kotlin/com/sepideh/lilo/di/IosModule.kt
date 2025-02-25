package com.sepideh.lilo.di

import com.sepideh.lilo.database.getTaskDatabaseBuilder
import org.koin.dsl.module

val iosModule = module {
    single { getTaskDatabaseBuilder().build() }
}