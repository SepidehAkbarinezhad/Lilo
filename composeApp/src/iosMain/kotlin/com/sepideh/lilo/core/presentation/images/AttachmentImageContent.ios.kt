package com.sepideh.lilo.core.presentation.images

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale

@Composable
internal actual fun AttachmentImageContent(
    image: AttachmentPreview,
    thumbnail: Boolean,
    description: String,
    modifier: Modifier,
    loading: @Composable () -> Unit,
    error: @Composable () -> Unit,
) {
    val loaded by rememberAttachmentBitmap(image, thumbnail)
    val bitmap = loaded.bitmap
    Box(modifier) {
        if (bitmap != null) {
            Image(bitmap, description, Modifier.fillMaxSize(),
                contentScale = if (thumbnail) ContentScale.Crop else ContentScale.Fit)
        } else if (loaded.loading) {
            loading()
        } else {
            error()
        }
    }
}
