package com.sepideh.lilo.core.data.images

import com.sepideh.lilo.core.domain.images.*
import kotlinx.coroutines.CancellationException
import kotlin.coroutines.*
import kotlin.test.*

class ImageImportTest {
    @Test fun importsPreserveSelectionOrder() {
        val names = immediate { importImageBatch(listOf(ImageSource("first"), ImageSource("second")), copy = { it.location }, remove = { error("Unexpected deletion") }) }
        assertEquals(listOf("first", "second"), names)
    }

    @Test fun failureRollsBackOnlyThisBatch() {
        val removed = mutableListOf<String>()
        assertFailsWith<IllegalStateException> {
            immediate { importImageBatch(listOf(ImageSource("first"), ImageSource("broken")), copy = {
                if (it.location == "broken") error("Copy failed")
                it.location
            }, remove = { removed.add(it) }) }
        }
        assertEquals(listOf("first"), removed)
    }

    @Test fun cancellationCleansUpAndRemainsCancellation() {
        val removed = mutableListOf<String>()
        assertFailsWith<CancellationException> {
            immediate { importImageBatch(listOf(ImageSource("first"), ImageSource("cancel")), copy = {
                if (it.location == "cancel") throw CancellationException("Left form")
                it.location
            }, remove = { removed.add(it) }) }
        }
        assertEquals(listOf("first"), removed)
    }

    @Test fun selectionLimitRejectsBatchBeforeAnyCopy() {
        assertFailsWith<IllegalArgumentException> {
            immediate { importImageBatch(List(IMAGE_SELECTION_LIMIT + 1) { ImageSource("$it") }, copy = { error("Must not copy") }, remove = {}) }
        }
    }

    @Test fun storedNamesCannotEscapePrivateDirectory() {
        listOf("", ".", "..", "../photo.jpg", "nested/photo.jpg", "nested\\photo.jpg").forEach { name ->
            assertFailsWith<IllegalArgumentException> { requireImageName(name) }
        }
        requireImageName("photo.jpg")
    }
}

/** These tests use synchronous copy callbacks; no dispatcher or native file system is needed. */
private fun <T> immediate(block: suspend () -> T): T {
    var outcome: Result<T>? = null
    block.startCoroutine(object : Continuation<T> {
        override val context: CoroutineContext = EmptyCoroutineContext
        override fun resumeWith(result: Result<T>) { outcome = result }
    })
    return requireNotNull(outcome) { "Unexpected asynchronous callback" }.getOrThrow()
}
