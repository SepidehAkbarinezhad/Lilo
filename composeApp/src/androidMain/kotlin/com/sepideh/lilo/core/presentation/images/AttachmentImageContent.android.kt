package com.sepideh.lilo.core.presentation.images

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.maxBitmapSize
import coil3.size.Size

@Composable
internal actual fun AttachmentImageContent(
    image: AttachmentPreview,
    thumbnail: Boolean,
    description: String,
    modifier: Modifier,
    loading: @Composable () -> Unit,
    error: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val request = remember(context, image, thumbnail) {
        // AsyncImage resolves the display size. These caps retain the previous decoder's
        // maximum dimensions, including for very wide/tall images rendered with Crop.
        val maxDimension = if (thumbnail) 640 else 3200
        ImageRequest.Builder(context)
            .data(image)
            .maxBitmapSize(Size(maxDimension, maxDimension))
            .build()
    }
    // Mirror Coil's state; a size/content-scale change may reuse a successful painter
    // without emitting another Success, so do not reset this when the request changes.
    var state by remember { mutableStateOf<AsyncImagePainter.State>(AsyncImagePainter.State.Empty) }

    Box(modifier) {
        AsyncImage(
            model = request,
            contentDescription = description.takeIf { state is AsyncImagePainter.State.Success },
            modifier = Modifier.fillMaxSize(),
            contentScale = if (thumbnail) ContentScale.Crop else ContentScale.Fit,
            onState = { state = it },
        )
        when (state) {
            is AsyncImagePainter.State.Empty, is AsyncImagePainter.State.Loading -> loading()
            is AsyncImagePainter.State.Error -> error()
            is AsyncImagePainter.State.Success -> Unit
        }
    }
}
