package com.sepideh.lilo.task.presentation.task_detail

import com.sepideh.lilo.core.presentation.BaseEvent
import com.sepideh.lilo.task.domain.Task

sealed interface TaskDetailEvent : BaseEvent {
    data class OnTitleChanged(val title : String):TaskDetailEvent
    data class OnDescriptionChanged(val description : String):TaskDetailEvent
    data class OnAddTask(val task: Task):TaskDetailEvent
}