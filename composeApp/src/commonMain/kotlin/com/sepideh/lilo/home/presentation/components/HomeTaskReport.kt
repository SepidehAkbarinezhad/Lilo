package com.sepideh.lilo.home.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextDecoration
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.core.presentation.components.LiloCheckbox
import com.sepideh.lilo.home.presentation.model.TaskReportDetail
import com.sepideh.lilo.ui.theme.LiloExtendedTheme
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun HomeTaskReport(detail: TaskReportDetail, actionColor: Color,
    completingIds: Set<Long>, onComplete: (Long) -> Unit, onListClick: () -> Unit, onLatestClick: (() -> Unit)?) {
    Surface(onClick = onListClick, shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface.copy(alpha = .65f)) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            HomeCount(detail.subTitleReportCount, Res.string.home_total_tasks, Modifier.weight(1f))
            HomeSummaryDivider()
            HomeCount(detail.completedCount, Res.string.home_completed_tasks, Modifier.weight(1f))
            HomeSummaryDivider()
            HomeCount(detail.remainingCount, Res.string.home_remaining_tasks, Modifier.weight(1f))
        }
    }
    if (detail.nextTaskTitle.isNullOrBlank()) {
        AppText(text = Res.string.home_no_tasks,
            color = LiloExtendedTheme.colors.textSecondary, modifier = Modifier.padding(vertical = 8.dp))
    } else HomeNextTask(detail, actionColor, !detail.nextTaskDone && detail.nextTaskId != null && detail.nextTaskId !in completingIds, onLatestClick) {
        detail.nextTaskId?.let(onComplete)
    }
}

@Composable
private fun HomeNextTask(detail: TaskReportDetail, actionColor: Color, enabled: Boolean, onLatestClick: (() -> Unit)?, onComplete: () -> Unit) {
    val colors = LiloExtendedTheme.colors
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        LiloCheckbox(checked = detail.nextTaskDone, enabled = enabled,
            label = stringResource(if (detail.nextTaskDone) Res.string.home_completed_tasks else Res.string.task_complete_action),
            accent = actionColor, onCheckedChange = { onComplete() })
        Column(Modifier.weight(1f).clickable(enabled = onLatestClick != null, role = Role.Button) { onLatestClick?.invoke() }, verticalArrangement = Arrangement.spacedBy(2.dp)) {
            AppText(text = detail.nextTaskTitle.orEmpty(), textType = TextType.BodyLarge,
                color = if (detail.nextTaskDone) colors.textSecondary else colors.title, maxLines = 2,
                textDecoration = if (detail.nextTaskDone) TextDecoration.LineThrough else TextDecoration.None)
            if (detail.nextTaskDescription.isNotBlank()) {
                AppText(text = detail.nextTaskDescription.replace('\n', ' ').replace('\r', ' ').trim(),
                    textType = TextType.Caption, color = colors.textSecondary, maxLines = 1)
            }
        }
    }
}
