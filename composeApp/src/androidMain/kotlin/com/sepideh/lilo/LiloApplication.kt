package com.sepideh.lilo

import android.app.Application
import com.sepideh.lilo.di.initKoin
import org.koin.android.ext.koin.androidContext

class LiloApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@LiloApplication)
        }
    }
}