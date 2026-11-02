package com.sepideh.lilo.core.presentation.images

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** Platform loading/rendering; the gallery and its loading/error UI remain shared. */
@Composable
internal expect fun AttachmentImageContent(
    image: AttachmentPreview,
    thumbnail: Boolean,
    description: String,
    modifier: Modifier,
    loading: @Composable () -> Unit,
    error: @Composable () -> Unit,
)
