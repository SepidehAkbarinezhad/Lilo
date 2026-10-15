package com.sepideh.lilo.core.utils

import androidx.compose.runtime.Composable

/** Applies the platform locale before Compose resolves localized resources. */
@Composable
expect fun LocalizedContent(languageCode: String, content: @Composable () -> Unit)
