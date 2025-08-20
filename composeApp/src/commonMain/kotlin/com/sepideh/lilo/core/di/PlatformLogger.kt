package com.sepideh.lilo.core.di

import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.MESSAGE

/**
 * Custom Koin [Logger] that prints logs to the console using `println`.
 * Used by Koin internally to print logs about the dependency injection process.
 */
class PlatformLogger : Logger(Level.DEBUG) {
    override fun display(level: Level, msg: MESSAGE) {
        when (level) {
            Level.DEBUG -> println("DEBUG: $msg")
            Level.INFO -> println("INFO: $msg")
            Level.ERROR -> println("ERROR: $msg")
            else -> println("LOG: $msg")
        }
    }
}