package com.sepideh.lilo.core.di

import com.sepideh.lilo.task.di.taskGroupModule
import com.sepideh.lilo.task.di.taskGroupPlatformModule
import com.sepideh.lilo.home.di.homeModule
import com.sepideh.lilo.note.di.noteModule
import com.sepideh.lilo.note.di.notePlatformModule
import com.sepideh.lilo.settings.di.settingsModule
import com.sepideh.lilo.task.di.taskModule
import com.sepideh.lilo.task.di.taskPlatformModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) {

    startKoin {
        config?.invoke(this)
        //sets Koin's internal logger
        logger(PlatformLogger())
        modules(
            homeModule,
            coreModule,
            settingsModule,
            taskGroupModule,
            corePlatformModule(),
            taskPlatformModule(),
            notePlatformModule(),
            taskGroupPlatformModule(),
            taskModule,
            noteModule
        )
    }
}

