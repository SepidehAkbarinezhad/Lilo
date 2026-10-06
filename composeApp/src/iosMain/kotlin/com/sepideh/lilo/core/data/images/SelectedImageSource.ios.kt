package com.sepideh.lilo.core.data.images

import com.sepideh.lilo.core.domain.images.ImageSource
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.*

/** Copy before the NSItemProvider completion returns: the provider deletes its temporary URL. */
@OptIn(ExperimentalForeignApi::class)
internal suspend fun loadSelectedImage(provider: NSItemProvider): ImageSource = suspendCancellableCoroutine { continuation ->
    if (!provider.hasItemConformingToTypeIdentifier("public.image")) {
        continuation.resumeWith(Result.failure(IllegalArgumentException("Selected item is not an image")))
        return@suspendCancellableCoroutine
    }
    val progress = provider.loadFileRepresentationForTypeIdentifier("public.image") { url, error ->
        if (error != null || url == null) {
            if (continuation.isActive) continuation.resumeWith(Result.failure(IllegalStateException(error?.localizedDescription ?: "Image is unavailable")))
        } else {
            val manager = NSFileManager.defaultManager
            val directory = (NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, true).first() as String) + "/selected-images"
            val extension = url.pathExtension?.takeIf { it.all { char -> char.isLetterOrDigit() } } ?: "img"
            val path = "$directory/${NSUUID().UUIDString}.$extension"
            val created = manager.createDirectoryAtPath(directory, true, null, null)
            val copied = created && manager.copyItemAtURL(url, NSURL.fileURLWithPath(path), null)
            if (copied && continuation.isActive) {
                continuation.resume(ImageSource(path), onCancellation = { _, source, _ -> removeSelectedImageSource(source.location) })
            } else {
                removeSelectedImageSource(path)
                if (continuation.isActive) continuation.resumeWith(Result.failure(IllegalStateException("Cannot read selected image")))
            }
        }
    }
    continuation.invokeOnCancellation { progress.cancel() }
}

@OptIn(ExperimentalForeignApi::class)
internal fun removeSelectedImageSource(path: String) {
    NSFileManager.defaultManager.removeItemAtPath(path, null)
}
