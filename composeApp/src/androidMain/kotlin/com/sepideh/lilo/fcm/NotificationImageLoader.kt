package com.sepideh.lilo.fcm

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.net.URL
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicReference
import javax.net.ssl.HttpsURLConnection

internal object NotificationImageLoader {
    private const val MAX_BYTES = 1024 * 1024
    private const val MAX_DIMENSION = 1024

    fun load(url: URL?): Bitmap? {
        if (url == null) return null
        val connection = AtomicReference<HttpsURLConnection?>()
        val executor = Executors.newSingleThreadExecutor()
        val future = executor.submit<Bitmap?> {
            val request = (url.openConnection() as HttpsURLConnection).apply {
                connectTimeout = 1000
                readTimeout = 1000
                instanceFollowRedirects = false // Never redirect to an unchecked URL.
                useCaches = false
            }
            connection.set(request)
            try {
                if (Thread.currentThread().isInterrupted) return@submit null
                if (request.responseCode != 200 || request.contentLengthLong > MAX_BYTES) return@submit null
                val bytes = request.inputStream.use { input ->
                    val output = ByteArrayOutputStream()
                    val buffer = ByteArray(8192)
                    while (!Thread.currentThread().isInterrupted) {
                        val count = input.read(buffer)
                        if (count == -1) break
                        if (output.size() + count > MAX_BYTES) return@submit null
                        output.write(buffer, 0, count)
                    }
                    output.toByteArray()
                }
                if (Thread.currentThread().isInterrupted) return@submit null
                val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
                if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return@submit null
                var sample = 1
                while (bounds.outWidth / sample > MAX_DIMENSION || bounds.outHeight / sample > MAX_DIMENSION) {
                    sample *= 2
                }
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size,
                    BitmapFactory.Options().apply { inSampleSize = sample })
            } finally {
                request.disconnect()
            }
        }
        return try {
            // Bound the whole attempt, including DNS and decoding, within FCM's short callback.
            future.get(2, TimeUnit.SECONDS)
        } catch (_: TimeoutException) {
            null
        } catch (_: ExecutionException) {
            null
        } catch (_: InterruptedException) {
            Thread.currentThread().interrupt()
            null
        } finally {
            future.cancel(true)
            executor.shutdownNow()
            connection.get()?.disconnect()
        }
    }
}
