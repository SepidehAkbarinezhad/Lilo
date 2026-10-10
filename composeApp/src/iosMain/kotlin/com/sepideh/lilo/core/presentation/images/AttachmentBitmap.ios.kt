package com.sepideh.lilo.core.presentation.images

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.sepideh.lilo.core.domain.images.requireImageName
import kotlinx.cinterop.*
import kotlinx.coroutines.*
import platform.CoreGraphics.*
import platform.Foundation.*
import platform.UIKit.*

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberAttachmentBitmap(image: AttachmentPreview, thumbnail: Boolean): State<AttachmentBitmapState> =
    produceState(AttachmentBitmapState(), image, thumbnail) {
        value = AttachmentBitmapState()
        val decoded = withContext(Dispatchers.Default) {
            try {
                val path = if (image.stored) {
                    requireImageName(image.location)
                    val root = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true).first() as String
                    val current = "$root/images/${image.location}"
                    if (NSFileManager.defaultManager.fileExistsAtPath(current)) current else "$root/task-images/${image.location}"
                } else image.location
                val source = UIImage.imageWithContentsOfFile(path) ?: return@withContext null
                val width = source.size.useContents { width }
                val height = source.size.useContents { height }
                if (width <= 0 || height <= 0) return@withContext null
                val scale = minOf(1.0, (if (thumbnail) 320.0 else 1600.0) / maxOf(width, height))
                val size = CGSizeMake(width * scale, height * scale)
                val renderer = UIGraphicsImageRenderer(size = size, format = UIGraphicsImageRendererFormat().apply { this.scale = 1.0 })
                val resized = renderer.imageWithActions { source.drawInRect(CGRectMake(0.0, 0.0, width * scale, height * scale)) }
                val data = UIImagePNGRepresentation(resized) ?: return@withContext null
                val bytes = data.bytes?.reinterpret<ByteVar>()?.readBytes(data.length.toInt()) ?: return@withContext null
                org.jetbrains.skia.Image.makeFromEncoded(bytes).toComposeImageBitmap()
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { null }
        }
        value = AttachmentBitmapState(decoded, loading = false)
    }
