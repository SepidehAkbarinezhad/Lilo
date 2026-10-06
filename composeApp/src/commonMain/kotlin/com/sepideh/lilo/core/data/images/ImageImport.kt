package com.sepideh.lilo.core.data.images

import com.sepideh.lilo.core.domain.images.IMAGE_SELECTION_LIMIT
import com.sepideh.lilo.core.domain.images.ImageSource
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

/** Roll back a partially copied batch, including when the owning operation is cancelled. */
internal suspend fun importImageBatch(
    sources: List<ImageSource>,
    copy: suspend (ImageSource) -> String,
    remove: suspend (String) -> Unit,
): List<String> {
    require(sources.size <= IMAGE_SELECTION_LIMIT)
    val imported = mutableListOf<String>()
    try {
        sources.forEach { source ->
            currentCoroutineContext().ensureActive()
            imported.add(copy(source))
            currentCoroutineContext().ensureActive()
        }
        return imported.toList()
    } catch (failure: Throwable) {
        withContext(NonCancellable) {
            imported.forEach { name -> runCatching { remove(name) } }
        }
        throw failure
    }
}
