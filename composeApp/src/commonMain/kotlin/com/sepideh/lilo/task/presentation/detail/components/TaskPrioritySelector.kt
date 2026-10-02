package com.sepideh.lilo.task.presentation.detail.components

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
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AppText(text = Res.string.priority_label, textType = TextType.FieldLabel)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf(TaskPriority.LOW, TaskPriority.MEDIUM, TaskPriority.HIGH).map { Priority.getByValue(it) }.forEach { priority ->
                val selected = selectedId == priority.id
                val dot = if (priority.value == TaskPriority.MEDIUM) accent else priority.color
                FilterChip(selected = selected, onClick = { onSelect(priority) },
                    modifier = Modifier.weight(1f).heightIn(min = 44.dp), shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, if (selected) accent else MaterialTheme.colorScheme.outlineVariant),
                    enabled = enabled,
                    leadingIcon = { Box(Modifier.size(10.dp).background(dot, CircleShape)) },
                    label = { AppText(text = priority.title, textType = TextType.Body) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = accent.copy(alpha = .10f), selectedLabelColor = MaterialTheme.colorScheme.onSurface))
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
