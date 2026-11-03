package com.sepideh.lilo.password.data

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.PersistableBundle
import android.os.SystemClock
import com.sepideh.lilo.password.domain.clipboard.PasswordClipboard
import kotlinx.coroutines.*
import java.util.UUID

class AndroidPasswordClipboard(context: Context) : PasswordClipboard {
    private val clipboard = context.applicationContext.getSystemService(ClipboardManager::class.java)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val expiry = PasswordClipboardExpiry(scope, ::readExpiry, ::clearIfOwned,
        System::currentTimeMillis, SystemClock::elapsedRealtime)

    override fun copyPassword(value: String) {
        val clip = ClipData.newPlainText("Lilo", value).apply {
            description.extras = PersistableBundle().apply {
                putBoolean("android.content.extra.IS_SENSITIVE", true)
                putString(OWNER_TOKEN, UUID.randomUUID().toString())
                putLong(COPIED_WALL, System.currentTimeMillis())
                putLong(COPIED_ELAPSED, SystemClock.elapsedRealtime())
            }
        }
        clipboard.setPrimaryClip(clip)
        expiry.refresh()
    }

    /** Window focus, rather than onResume, is required for Android clipboard read access. */
    fun onWindowFocusChanged(hasFocus: Boolean) = expiry.onFocusChanged(hasFocus)

    private fun readExpiry(): PasswordClipExpiry? {
        val extras = clipboard.primaryClipDescription?.extras ?: return null
        val token = extras.getString(OWNER_TOKEN)?.takeIf { it.isNotBlank() } ?: return null
        // Clips from the previous implementation have a token but no deadline: expire on return.
        return PasswordClipExpiry(token, extras.getLong(COPIED_WALL, 0), extras.getLong(COPIED_ELAPSED, 0))
    }

    private fun clearIfOwned(token: String) {
        // Recheck immediately before clearing; never read or compare the secret itself.
        if (clipboard.primaryClipDescription?.extras?.getString(OWNER_TOKEN) != token) return
        if (Build.VERSION.SDK_INT >= 28) clipboard.clearPrimaryClip()
        else clipboard.setPrimaryClip(ClipData.newPlainText("", ""))
    }

    private companion object {
        const val OWNER_TOKEN = "com.sepideh.lilo.clipboard.owner"
        const val COPIED_WALL = "com.sepideh.lilo.clipboard.copiedWall"
        const val COPIED_ELAPSED = "com.sepideh.lilo.clipboard.copiedElapsed"
    }
}
