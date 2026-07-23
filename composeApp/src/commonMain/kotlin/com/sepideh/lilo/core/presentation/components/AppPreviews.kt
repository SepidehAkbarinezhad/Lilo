package com.sepideh.lilo.core.presentation.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_NO
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import com.sepideh.lilo.core.utils.LocalLanguageCode
import com.sepideh.lilo.ui.theme.LiloTheme

// 1. Compile-time constants for the @Preview locale parameters
object PreviewLocales {
    const val FA = "fa"
    const val EN = "en"
}


@Preview(locale = PreviewLocales.FA, uiMode = UI_MODE_NIGHT_NO)
@Preview(locale = PreviewLocales.FA, uiMode = UI_MODE_NIGHT_YES)
@Preview(locale = PreviewLocales.EN, uiMode = UI_MODE_NIGHT_NO)
@Preview(locale = PreviewLocales.EN, uiMode = UI_MODE_NIGHT_YES)
annotation class AppPreviews


@Composable
fun LiloPreviewWrapper(
    content: @Composable () -> Unit
) {
    val languageCode = LocalLanguageCode.current // Respects the preview's locale automatically
    val darkTheme = isSystemInDarkTheme()

    LiloTheme(darkTheme = darkTheme, languageCode = languageCode) {
        content()
    }
}