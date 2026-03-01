package com.sepideh.lilo.app

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.sepideh.lilo.core.di.initKoin
import org.koin.android.ext.koin.androidContext

class LiloApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidContext(this@LiloApplication)
        }

        FirebaseApp.initializeApp(this)

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    Log.d("FCM", "✅ Token received: $token")

                    subscribeToTopic()
                } else {
                    Log.e("FCM", "❌ Failed to get token", task.exception)
                }
            }
    }

    private fun subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("all_users")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FCM", "✅ Successfully subscribed to 'news' topic")
                } else {
                    Log.e("FCM", "❌ Subscription FAILED", task.exception)
                    Log.e("FCM", "Error message: ${task.exception?.message}")

                }
            }
    }
}