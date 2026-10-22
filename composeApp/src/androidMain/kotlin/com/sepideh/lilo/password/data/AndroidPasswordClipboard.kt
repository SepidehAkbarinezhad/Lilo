package com.sepideh.lilo.password.data

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.PersistableBundle
import com.sepideh.lilo.password.domain.clipboard.PasswordClipboard
import kotlinx.coroutines.*
import java.util.UUID

class AndroidPasswordClipboard(context: Context) : PasswordClipboard {
    private val clipboard = context.getSystemService(ClipboardManager::class.java)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var cleanup: Job? = null

    override fun copyPassword(value: String) {
        val token = UUID.randomUUID().toString()
        val clip = ClipData.newPlainText("Lilo", value).apply {
            description.extras = PersistableBundle().apply {
                putBoolean("android.content.extra.IS_SENSITIVE", true)
                putString(OWNER_TOKEN, token)
            }
        }
        clipboard.setPrimaryClip(clip)
        cleanup?.cancel()
        cleanup = scope.launch {
            delay(60_000)
            try {
                // Never clear content the user copied later, and do not retain the password in this job.
                if (clipboard.primaryClipDescription?.extras?.getString(OWNER_TOKEN) == token) {
                    if (Build.VERSION.SDK_INT >= 28) clipboard.clearPrimaryClip()
                    else clipboard.setPrimaryClip(ClipData.newPlainText("", ""))
                }
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { /* Android can restrict clipboard access while the app is backgrounded. */ }
        }
    }

    private companion object { const val OWNER_TOKEN = "com.sepideh.lilo.clipboard.owner" }
}
