package com.sepideh.lilo.note.di

import com.sepideh.lilo.note.data.local.room.getNoteDatabaseBuilder
import com.sepideh.lilo.task.data.local.room.getTaskDatabaseBuilder
import com.sepideh.lilo.task.di.taskDatabaseQualifier
import org.koin.dsl.module

actual fun notePlatformModule() = module {
    single(noteDatabaseQualifier) { getNoteDatabaseBuilder().build() }
}