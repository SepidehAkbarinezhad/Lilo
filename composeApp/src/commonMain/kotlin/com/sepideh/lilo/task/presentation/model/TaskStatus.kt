package com.sepideh.lilo.task.presentation.model

import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.all
import lilo.composeapp.generated.resources.status_Undone_label
import lilo.composeapp.generated.resources.status_done_label
import org.jetbrains.compose.resources.StringResource

enum class TaskStatus(val label: StringResource) {
    DONE(Res.string.status_done_label),
    UNDONE(Res.string.status_Undone_label),
    ALL(Res.string.all)
}