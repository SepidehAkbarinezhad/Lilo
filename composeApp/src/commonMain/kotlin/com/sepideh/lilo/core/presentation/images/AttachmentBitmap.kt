package com.sepideh.lilo.core.presentation.images

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.ImageBitmap

data class AttachmentBitmapState(val bitmap: ImageBitmap? = null, val loading: Boolean = true)

/** Bounded native decoding keeps original camera images out of thumbnail memory. */
@Composable
expect fun rememberAttachmentBitmap(image: AttachmentPreview, thumbnail: Boolean): State<AttachmentBitmapState>
