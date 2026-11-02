package com.sepideh.lilo.core.presentation.images

import coil3.map.Mapper
import coil3.request.Options
import com.sepideh.lilo.core.domain.images.requireImageName
import java.io.File

/** Runs on the image loader's IO interceptor context, before Coil's standard URI/file loading. */
internal class AttachmentPreviewMapper : Mapper<AttachmentPreview, String> {
    override fun map(data: AttachmentPreview, options: Options): String =
        resolveAttachmentLocation(options.context.filesDir, data)
}

internal fun resolveAttachmentLocation(filesDir: File, image: AttachmentPreview): String {
    if (!image.stored) return image.location

    requireImageName(image.location)
    val current = File(File(filesDir, "images"), image.location)
    val file = if (current.exists()) current else File(File(filesDir, "task-images"), image.location)
    return file.absolutePath
}
