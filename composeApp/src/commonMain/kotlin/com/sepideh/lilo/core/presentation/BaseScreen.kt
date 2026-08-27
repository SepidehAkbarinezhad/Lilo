package com.sepideh.lilo.core.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.components.AppPreviews
import com.sepideh.lilo.core.presentation.components.LiloPreviewWrapper
import com.sepideh.lilo.ui.theme.LocalLiloColorsPalette
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.app_name
import lilo.composeapp.generated.resources.title_label

@Composable
fun BaseScreen(
    header: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    val palette = LocalLiloColorsPalette.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier.fillMaxSize().background(palette.headerSurface) .windowInsetsPadding(WindowInsets.statusBars)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                keyboardController?.hide()
            },
    ) {
        header()

        Surface(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                content()
            }
        }
    }

}

@AppPreviews
@Composable
private fun BaseScreenPreview() {
    LiloPreviewWrapper {
        BaseScreen(
          header = {
              BaseHeader(
                  title = Res.string.app_name,
                  mainScreen = false,
                  onBackPressed = {true}
              )
          },
            content = {}
        )
    }
}


