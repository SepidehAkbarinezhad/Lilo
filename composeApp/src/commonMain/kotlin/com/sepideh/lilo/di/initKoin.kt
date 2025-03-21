package com.sepideh.lilo.di

import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.MESSAGE
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config : KoinAppDeclaration?= null){

    startKoin {
        config?.invoke(this)
        logger(PlatformLogger())
        modules(provideViewModelModule, platformModule())
    }
}

class PlatformLogger : Logger(Level.DEBUG) {
    override fun display(level: Level, msg: MESSAGE) {
        when (level) {
            Level.DEBUG -> println("DEBUG: $msg")  // Logs to the console
            Level.INFO -> println("INFO: $msg")  // Logs to the console
            Level.ERROR -> println("ERROR: $msg")  // Logs to the console
            else -> println("LOG: $msg")
        }
    }
}