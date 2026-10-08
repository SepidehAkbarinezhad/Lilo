package com.sepideh.lilo.core.presentation.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.sepideh.lilo.task.presentation.TaskGroupUi
import com.sepideh.lilo.note.presentation.detail.components.PermissionDeniedDialog
import com.sepideh.lilo.task.presentation.detail.TaskDetailState
import com.sepideh.lilo.task.presentation.detail.components.PermissionDeniedDialog


@Preview
@Composable
fun PermissionDeniedDialogPreview() {
    PermissionDeniedDialog(
        state = TaskDetailState(
            selectedGroup = TaskGroupUi(id = 1, title = "کار", isEditable = false))) {
    }
}

