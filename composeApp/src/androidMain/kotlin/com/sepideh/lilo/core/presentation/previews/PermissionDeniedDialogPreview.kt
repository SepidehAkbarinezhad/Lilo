package com.sepideh.lilo.core.presentation.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.sepideh.lilo.task.presentation.task_detail.TaskDetailState
import com.sepideh.lilo.task.presentation.task_detail.components.PermissionDeniedDialog


@Preview
@Composable
fun PermissionDeniedDialogPreview() {
   PermissionDeniedDialog(state = TaskDetailState()) { }
}

