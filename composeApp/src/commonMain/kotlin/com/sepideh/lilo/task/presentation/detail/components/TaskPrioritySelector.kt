package com.sepideh.lilo.task.presentation.detail.components

import com.sepideh.lilo.ui.theme.*

import com.sepideh.lilo.core.presentation.icons.LiloIcons

import androidx.compose.foundation.selection.selectable
import androidx.compose.ui.Alignment
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.*
import com.sepideh.lilo.task.presentation.model.Priority
import com.sepideh.lilo.ui.theme.LiloExtendedTheme
import lilo.composeapp.generated.resources.*

import com.sepideh.lilo.task.domain.model.TaskPriority

@Composable
fun TaskPrioritySelector(selectedId: Int, accent: Color, enabled: Boolean = true, onSelect: (Priority) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(LiloSpacing.Small)) {
        AppText(text = Res.string.priority_label, textType = TextType.FieldLabel, color = LiloExtendedTheme.colors.textSecondary)
        Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.surface, shadowElevation = 1.dp) {
            Row(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                listOf(TaskPriority.LOW, TaskPriority.MEDIUM, TaskPriority.HIGH).map { Priority.getByValue(it) }.forEach { priority ->
                    val selected = selectedId == priority.id
                    Row(Modifier.weight(1f).heightIn(min = 48.dp)
                        .selectable(selected = selected, enabled = enabled, role = Role.RadioButton, onClick = { onSelect(priority) })
                        .padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center) {
                        Box(Modifier.size(22.dp).background(if (selected) priority.color else priority.color.copy(alpha = .65f), CircleShape), contentAlignment = Alignment.Center) {
                            if (selected) Icon(LiloIcons.Check, null, Modifier.size(16.dp), tint = LiloExtendedTheme.colors.onPrioritySelected)
                        }
                        Spacer(Modifier.width(LiloSpacing.Small))
                        AppText(text = priority.title, textType = TextType.Body,
                            color = if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@AppPreviews
@Composable
private fun TaskPrioritySelectorPreview() {
    LiloPreviewWrapper {
        TaskPrioritySelector(TaskPriority.MEDIUM.id, LiloExtendedTheme.colors.taskColor) {}
    }
}
