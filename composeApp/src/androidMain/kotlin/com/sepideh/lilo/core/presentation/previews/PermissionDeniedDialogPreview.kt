package com.sepideh.lilo.core.presentation.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.sepideh.lilo.category.domain.CategoryDomain
import com.sepideh.lilo.category.domain.toPresentation
import com.sepideh.lilo.core.domain.model.AppLanguage
import com.sepideh.lilo.task.presentation.task_detail.TaskDetailState
import com.sepideh.lilo.task.presentation.task_detail.components.PermissionDeniedDialog


@Preview
@Composable
fun PermissionDeniedDialogPreview() {
    PermissionDeniedDialog(
        state = TaskDetailState(
            selectedCategory = CategoryDomain.categories[0].toPresentation(AppLanguage.FA))) {
    }
}

