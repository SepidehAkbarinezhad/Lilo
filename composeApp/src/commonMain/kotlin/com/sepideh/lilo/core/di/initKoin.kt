package com.sepideh.lilo.core.di

import com.sepideh.lilo.category.di.categoryModule
import com.sepideh.lilo.category.di.categoryPlatformModule
import com.sepideh.lilo.settings.di.settingsModule
import com.sepideh.lilo.task.di.taskPlatformModule
import com.sepideh.lilo.task.di.viewModelModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config : KoinAppDeclaration?= null){

    startKoin {
        config?.invoke(this)
        //sets Koin's internal logger
        logger(PlatformLogger())
        modules(
            settingsModule,
            coreModule,
            categoryModule,
            corePlatformModule(),
            taskPlatformModule(),
            categoryPlatformModule(),
            viewModelModule
        )
    }
}

