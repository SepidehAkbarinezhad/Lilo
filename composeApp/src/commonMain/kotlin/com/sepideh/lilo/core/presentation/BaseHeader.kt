package com.sepideh.lilo.core.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.components.AppPreviews
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.core.presentation.components.LiloPreviewWrapper
import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.task.presentation.detail.TaskDetailScreen
import com.sepideh.lilo.task.presentation.detail.TaskDetailState
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.tasks_list_title
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun BaseHeader(
    modifier: Modifier = Modifier,
    title: StringResource,
    mainScreen: Boolean = false,
    onBackPressed: () -> Boolean = { true }
) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val contentColor = MaterialTheme.colorScheme.onBackground

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        AppText(
            modifier = Modifier.padding(horizontal = 48.dp),
            text = title,
            textType = TextType.Title,
            color = contentColor
        )

        if (!mainScreen) {
            IconButton(
                modifier = Modifier.align(Alignment.CenterStart),
                onClick = { onBackPressed() }
            ) {
                Icon(
                    imageVector = if (isRtl) {
                        Icons.Default.ArrowForwardIos
                    } else {
                        Icons.Default.ArrowBackIosNew
                    },
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@AppPreviews
@Composable
private fun TaskDetailPreview() {
    LiloPreviewWrapper {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            BaseHeader(
                title = Res.string.tasks_list_title,
                mainScreen = false,
                onBackPressed = { true }
            )
        }

    }
}
