package com.sepideh.lilo.task.presentation.model

import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.all
import lilo.composeapp.generated.resources.sort_date_label
import lilo.composeapp.generated.resources.sort_priority_label
import lilo.composeapp.generated.resources.status_Undone_label
import lilo.composeapp.generated.resources.status_done_label
import org.jetbrains.compose.resources.StringResource

enum class Enums(val label: StringResource) {
    DONE(Res.string.status_done_label),
    UNDONE(Res.string.status_Undone_label),
    ALL(Res.string.all)
}

enum class SortOrder(val label: StringResource) {
    Priority(Res.string.sort_priority_label),
    Date(Res.string.sort_date_label)
}