package com.sepideh.lilo.core.presentation.components.picker

import androidx.activity.compose.rememberLauncherForActivityResult
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
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        picking = false
        // SAF does not enforce a selection limit, so retain only the allowed images.
        if (uris.isNotEmpty()) selected(uris.take(limit).map { ImageSource(it.toString()) })
    }
    val singleLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        picking = false
        if (uri != null) selected(listOf(ImageSource(uri.toString())))
    }
    return {
        if (!picking) {
            picking = true
            try {
                val imageTypes = arrayOf("image/*")
                if (limit == 1) singleLauncher.launch(imageTypes) else launcher.launch(imageTypes)
            }
            catch (_: Exception) { picking = false; error() }
        }
    }
}
