package com.sepideh.lilo.core.presentation.images

import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import com.sepideh.lilo.core.domain.images.requireImageName
import kotlinx.coroutines.*
import java.io.File
import java.io.InputStream

@Composable
actual fun rememberAttachmentBitmap(image: AttachmentPreview, thumbnail: Boolean): State<AttachmentBitmapState> {
    val context = LocalContext.current.applicationContext
    return produceState(AttachmentBitmapState(), image, thumbnail) {
        value = AttachmentBitmapState()
        val decoded = withContext(Dispatchers.IO) {
            try {
                fun open(): InputStream {
                    if (!image.stored) return requireNotNull(context.contentResolver.openInputStream(Uri.parse(image.location)))
                    requireImageName(image.location)
                    val current = File(File(context.filesDir, "images"), image.location)
                    val file = if (current.exists()) current else File(File(context.filesDir, "task-images"), image.location)
                    return file.inputStream()
                }
                val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                open().use { BitmapFactory.decodeStream(it, null, bounds) }
                val max = if (thumbnail) 320 else 1600
                var sample = 1
                while (maxOf(bounds.outWidth, bounds.outHeight) / sample > max * 2) sample *= 2
                val bitmap = open().use { BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = sample }) }
                    ?: return@withContext null
                val orientation = runCatching { open().use { ExifInterface(it).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL) } }.getOrDefault(ExifInterface.ORIENTATION_NORMAL)
                val matrix = Matrix().apply {
                    when (orientation) {
                        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> setScale(-1f, 1f)
                        ExifInterface.ORIENTATION_ROTATE_180 -> setRotate(180f)
                        ExifInterface.ORIENTATION_FLIP_VERTICAL -> setScale(1f, -1f)
                        ExifInterface.ORIENTATION_TRANSPOSE -> { setRotate(90f); postScale(-1f, 1f) }
                        ExifInterface.ORIENTATION_ROTATE_90 -> setRotate(90f)
                        ExifInterface.ORIENTATION_TRANSVERSE -> { setRotate(270f); postScale(-1f, 1f) }
                        ExifInterface.ORIENTATION_ROTATE_270 -> setRotate(270f)
                    }
                }
                val oriented = if (matrix.isIdentity) bitmap else android.graphics.Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                if (oriented !== bitmap) bitmap.recycle()
                oriented.asImageBitmap()
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { null }
        }
        value = AttachmentBitmapState(decoded, loading = false)
    }
}
