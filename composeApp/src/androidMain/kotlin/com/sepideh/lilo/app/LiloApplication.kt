package com.sepideh.lilo.app

import android.app.Application
import android.content.Context
import android.util.Log
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.memory.MemoryCache
import com.google.firebase.FirebaseApp
import com.sepideh.lilo.fcm.LiloFirebaseMessagingService
import com.google.firebase.messaging.FirebaseMessaging
import com.sepideh.lilo.core.di.initKoin
import com.sepideh.lilo.core.presentation.images.AttachmentPreviewMapper
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext

class LiloApplication : Application(), SingletonImageLoader.Factory {

    override fun newImageLoader(context: Context): ImageLoader = ImageLoader.Builder(context)
        .components { add(AttachmentPreviewMapper()) }
        .interceptorCoroutineContext(Dispatchers.IO)
        .memoryCache { MemoryCache.Builder().maxSizePercent(context, 0.20).build() }
        .diskCache(null) // Attachments already live in private storage; no network sources.
        .build()

    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidContext(this@LiloApplication)
        }

        FirebaseApp.initializeApp(this)

        LiloFirebaseMessagingService.createNotificationChannel(this)

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    subscribeToTopic()
                } else {
                    Log.e("FCM", "Failed to get messaging token")
                }
            }
    }

    private fun subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("all_users")
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e("FCM", "Failed to subscribe to all_users")
                }
            }
    }
}
