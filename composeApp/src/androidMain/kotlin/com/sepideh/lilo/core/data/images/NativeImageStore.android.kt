package com.sepideh.lilo.core.data.images

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import com.sepideh.lilo.core.domain.images.ImageSource
import com.sepideh.lilo.core.domain.images.ImageStore
import com.sepideh.lilo.core.domain.images.requireImageName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import kotlin.coroutines.coroutineContext

class NativeImageStore(context: Context) : ImageStore {
    private val appContext = context.applicationContext
    private val directory = File(appContext.filesDir, "images")
    // Preserve access to images saved by the initial attachment implementation.
    private val legacyDirectory = File(appContext.filesDir, "task-images")

    override suspend fun importImages(sources: List<ImageSource>): List<String> = withContext(Dispatchers.IO) {
        check(directory.isDirectory || directory.mkdirs()) { "Cannot create image storage" }
        importImageBatch(sources, copy = { source ->
            val uri = Uri.parse(source.location)
            val mime = appContext.contentResolver.getType(uri)
            require(mime?.startsWith("image/") == true) { "Source is not an image" }
            val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mime) ?: "img"
            val target = File(directory, "${UUID.randomUUID()}.$extension")
            try {
                val input = requireNotNull(appContext.contentResolver.openInputStream(uri)) { "Image is unavailable" }
                input.use { from ->
                    target.outputStream().use { to ->
                        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                        while (true) {
                            coroutineContext.ensureActive()
                            val count = from.read(buffer)
                            if (count < 0) break
                            to.write(buffer, 0, count)
                        }
                    }
                }
                target.name
            } catch (failure: Throwable) {
                target.delete()
                throw failure
            }
        }, remove = { name -> deleteImage(name) })
    }

    override suspend fun removeImages(names: List<String>) = withContext(Dispatchers.IO) {
        names.forEach { deleteImage(it) }
    }

    private fun deleteImage(name: String) {
        requireImageName(name)
        listOf(directory, legacyDirectory).forEach { folder ->
            val file = File(folder, name)
            check(!file.exists() || file.delete()) { "Cannot remove image" }
        }
    }
}
