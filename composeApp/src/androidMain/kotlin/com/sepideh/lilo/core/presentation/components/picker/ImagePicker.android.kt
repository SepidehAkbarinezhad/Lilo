package com.sepideh.lilo.core.presentation.components.picker

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import com.sepideh.lilo.core.domain.images.IMAGE_SELECTION_LIMIT
import com.sepideh.lilo.core.domain.images.ImageSource

@Composable
actual fun rememberImagePicker(onSelected: (List<ImageSource>) -> Unit, onError: () -> Unit): () -> Unit {
    val selected by rememberUpdatedState(onSelected)
    val error by rememberUpdatedState(onError)
    var picking by rememberSaveable { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(IMAGE_SELECTION_LIMIT)) { uris ->
        picking = false
        // ACTION_OPEN_DOCUMENT fallback does not enforce the native selection limit.
        if (uris.isNotEmpty()) selected(uris.take(IMAGE_SELECTION_LIMIT).map { ImageSource(it.toString()) })
    }
    return {
        if (!picking) {
            picking = true
            try { launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
            catch (_: Exception) { picking = false; error() }
        }
    }
}
