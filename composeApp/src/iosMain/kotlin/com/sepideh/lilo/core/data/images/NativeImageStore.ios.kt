package com.sepideh.lilo.core.data.images

import com.sepideh.lilo.core.domain.images.ImageSource
import com.sepideh.lilo.core.domain.images.ImageStore
import com.sepideh.lilo.core.domain.images.requireImageName
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Foundation.*

@OptIn(ExperimentalForeignApi::class)
class NativeImageStore : ImageStore {
    private val directory get() = documentsDirectory() + "/images"
    private val legacyDirectory get() = documentsDirectory() + "/task-images"

    override suspend fun importImages(sources: List<ImageSource>): List<String> = withContext(Dispatchers.Default) {
        val manager = NSFileManager.defaultManager
        check(manager.createDirectoryAtPath(directory, true, null, null)) { "Cannot create image storage" }
        importImageBatch(sources, copy = { source ->
            // iOS sources are copies of PHPicker's short-lived provider URLs in our cache.
            val url = NSURL.fileURLWithPath(source.location)
            val extension = url.pathExtension?.takeIf { it.all { char -> char.isLetterOrDigit() } } ?: "img"
            val name = "${NSUUID().UUIDString}.$extension"
            val path = "$directory/$name"
            try {
                check(manager.copyItemAtPath(source.location, path, null)) { "Cannot import image" }
                name
            } catch (failure: Throwable) {
                manager.removeItemAtPath(path, null)
                throw failure
            }
        }, remove = { name -> deleteImage(name) })
    }

    override suspend fun removeImages(names: List<String>) = withContext(Dispatchers.Default) {
        names.forEach { deleteImage(it) }
    }

    private fun deleteImage(name: String) {
        requireImageName(name)
        val manager = NSFileManager.defaultManager
        listOf(directory, legacyDirectory).forEach { folder ->
            val path = "$folder/$name"
            check(!manager.fileExistsAtPath(path) || manager.removeItemAtPath(path, null)) { "Cannot remove image" }
        }
    }
}

private fun documentsDirectory(): String =
    NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true).first() as String
