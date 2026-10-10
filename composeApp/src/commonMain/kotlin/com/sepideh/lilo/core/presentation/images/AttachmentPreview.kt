package com.sepideh.lilo.core.presentation.images

import com.sepideh.lilo.core.domain.images.ImageSource
import com.sepideh.lilo.core.domain.images.IMAGE_SELECTION_LIMIT

/** Stored names and temporary picker locations deliberately remain distinct. */
data class AttachmentPreview(val location: String, val stored: Boolean) {
    val key: String get() = if (stored) "stored:$location" else "selected:$location"
}

fun attachmentPreviews(names: List<String>, removed: Set<String>, selected: List<ImageSource>?): List<AttachmentPreview> =
    names.filterNot { it in removed }.map { AttachmentPreview(it, true) } +
        selected.orEmpty().map { AttachmentPreview(it.location, false) }

fun appendImageSelection(existing: List<ImageSource>?, added: List<ImageSource>, retainedCount: Int): List<ImageSource> {
    val result = (existing.orEmpty() + added).distinctBy { it.location }
    require(result.size + retainedCount <= IMAGE_SELECTION_LIMIT) { "Too many images" }
    return result
}
