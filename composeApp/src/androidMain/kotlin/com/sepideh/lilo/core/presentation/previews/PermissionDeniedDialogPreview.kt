package com.sepideh.lilo.core.presentation.previews
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.sepideh.lilo.task.domain.reminder.ReminderPermission
import com.sepideh.lilo.task.presentation.detail.components.PermissionDeniedDialog
@Preview @Composable fun PermissionDeniedDialogPreview() {
 PermissionDeniedDialog(ReminderPermission.NOTIFICATIONS) {}
}
