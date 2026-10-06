package com.sepideh.lilo.core.domain.images

/** An opaque native picker reference. Only the platform implementation interprets its location. */
data class ImageSource(val location: String)

/** Private image storage, independent of the feature that owns the attachment records. */
interface ImageStore {
    suspend fun importImages(sources: List<ImageSource>): List<String>
    suspend fun removeImages(names: List<String>)
}

const val IMAGE_SELECTION_LIMIT = 10

/** Stored names are relative file names, never user-supplied paths. */
fun requireImageName(name: String) {
    require(name.isNotBlank() && name != "." && name != ".." && '/' !in name && '\\' !in name)
}
