package com.sepideh.lilo.task.presentation.task_detail

import com.sepideh.lilo.core.presentation.BaseEvent

sealed interface TaskDetailEvent : BaseEvent {
    data class OnTitleChanged(val title: String) : TaskDetailEvent
    data class OnDescriptionChanged(val description: String) : TaskDetailEvent
    data class OnSelectedCategoryChanged(val title: String) : TaskDetailEvent
    data class OnSelectedPriorityChanged(val title: String) : TaskDetailEvent
    data class OnSelectReminderDate(val date: Pair<Long?,Long?>) : TaskDetailEvent
    data class OnSelectReminderTime(val  time: Pair<Int?,Int?>) : TaskDetailEvent
    data object OnAddTaskButton : TaskDetailEvent
}