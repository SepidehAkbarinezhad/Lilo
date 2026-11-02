package com.sepideh.lilo.core.presentation.images

import java.io.File
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AttachmentLocationTest {
    @Test
    fun selectedContentUriIsPreserved() = withFilesDir { root ->
        val uri = "content://media/picker/0/com.android.providers.media.photopicker/media/42"
        assertEquals(uri, resolveAttachmentLocation(root, AttachmentPreview(uri, stored = false)))
    }

    @Test
    fun currentStorageTakesPrecedenceOverLegacyStorage() = withFilesDir { root ->
        val current = File(root, "images/photo.jpg").apply { requireNotNull(parentFile).mkdirs(); writeText("current") }
        File(root, "task-images/photo.jpg").apply { requireNotNull(parentFile).mkdirs(); writeText("legacy") }
        assertEquals(current.absolutePath, resolveAttachmentLocation(root, stored("photo.jpg")))
    }

    @Test
    fun legacyFileRemainsReadableWithoutMovingIt() = withFilesDir { root ->
        val legacy = File(root, "task-images/old.jpg").apply { requireNotNull(parentFile).mkdirs(); writeText("legacy") }
        assertEquals(legacy.absolutePath, resolveAttachmentLocation(root, stored("old.jpg")))
        assertEquals("legacy", legacy.readText())
    }

    @Test
    fun missingFileStillResolvesSoCoilCanReportAnError() = withFilesDir { root ->
        assertEquals(File(root, "task-images/missing.jpg").absolutePath,
            resolveAttachmentLocation(root, stored("missing.jpg")))
    }

    @Test
    fun storedNamesCannotEscapePrivateImageDirectories() = withFilesDir { root ->
        listOf("", " ", ".", "..", "../photo.jpg", "nested/photo.jpg", "nested\\photo.jpg", "/tmp/photo.jpg")
            .forEach { name ->
                assertFailsWith<IllegalArgumentException>(name) { resolveAttachmentLocation(root, stored(name)) }
            }
    }

    private fun stored(name: String) = AttachmentPreview(name, stored = true)

    private fun withFilesDir(block: (File) -> Unit) {
        val root = Files.createTempDirectory("lilo-image-location-").toFile()
        try { block(root) } finally { root.deleteRecursively() }
    }
}
