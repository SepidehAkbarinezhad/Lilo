package com.sepideh.lilo.core.presentation.components.picker

import androidx.compose.runtime.Composable
import com.sepideh.lilo.core.domain.images.ImageSource

/**
 * Launches native multi-image selection. Cancellation leaves the current selection untouched.
 * Sources remain available for the current form session; ImageStore imports them on Save.
 * Remember at screen scope, outside transient dialogs and sheets.
 */
@Composable
expect fun rememberImagePicker(onSelected: (List<ImageSource>) -> Unit, onError: () -> Unit): () -> Unit
