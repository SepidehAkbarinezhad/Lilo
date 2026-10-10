package com.sepideh.lilo.core.presentation.images

import com.sepideh.lilo.core.domain.images.ImageSource
import kotlin.test.*

class AttachmentSelectionTest {
    @Test fun savedAndNewImagesRemainVisibleInOrder() {
        val saved = listOf("old.jpg", "removed.jpg")
        val previews = attachmentPreviews(saved, setOf("removed.jpg"), listOf(ImageSource("selected")))
        assertEquals(listOf(AttachmentPreview("old.jpg", true), AttachmentPreview("selected", false)), previews)
        assertEquals(listOf("old.jpg", "removed.jpg"), saved)
    }
    @Test fun appendPreservesPreviousSelectionWithoutDuplicates() {
        val previous = listOf(ImageSource("first"))
        assertEquals(listOf(ImageSource("first"), ImageSource("second")),
            appendImageSelection(previous, listOf(ImageSource("first"), ImageSource("second")), 2))
    }
    @Test fun totalLimitIncludesSavedImagesAndRejectsOverflow() {
        val previous = listOf(ImageSource("first"))
        assertFailsWith<IllegalArgumentException> { appendImageSelection(previous, listOf(ImageSource("second")), 9) }
        assertEquals(listOf(ImageSource("first")), previous)
    }
    @Test fun storedAndSelectedLocationsCannotCollideAsUiKeys() {
        assertNotEquals(AttachmentPreview("same", true).key, AttachmentPreview("same", false).key)
    }
}
