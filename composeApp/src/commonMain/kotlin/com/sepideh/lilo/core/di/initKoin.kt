package com.sepideh.lilo.core.di

import com.sepideh.lilo.task.di.taskPlatformModule
import com.sepideh.lilo.task.di.viewModelModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config : KoinAppDeclaration?= null){

    startKoin {
        config?.invoke(this)
        //sets Koin's internal logger
        logger(PlatformLogger())
        modules(viewModelModule, taskPlatformModule())
    }
}

