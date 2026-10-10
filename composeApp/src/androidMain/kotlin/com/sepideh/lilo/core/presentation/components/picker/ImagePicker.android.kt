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
actual fun rememberImagePicker(onSelected: (List<ImageSource>) -> Unit, onError: () -> Unit, maxSelection: Int): () -> Unit {
    require(maxSelection in 1..IMAGE_SELECTION_LIMIT)
    val limit by rememberUpdatedState(maxSelection)
    val selected by rememberUpdatedState(onSelected)
    val error by rememberUpdatedState(onError)
    var picking by rememberSaveable { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(maxOf(2, maxSelection))) { uris ->
        picking = false
        // ACTION_OPEN_DOCUMENT fallback does not enforce the native selection limit.
        if (uris.isNotEmpty()) selected(uris.take(limit).map { ImageSource(it.toString()) })
    }
    val singleLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        picking = false
        if (uri != null) selected(listOf(ImageSource(uri.toString())))
    }
    return {
        if (!picking) {
            picking = true
            try {
                val request = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                if (limit == 1) singleLauncher.launch(request) else launcher.launch(request)
            }
            catch (_: Exception) { picking = false; error() }
        }
    }
}
