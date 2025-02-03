package com.sepideh.lilo.task.presentation.task_list

import com.sepideh.lilo.task.domain.Task

sealed interface TaskListAction {
    data class OnTabSelected(val index : Int):TaskListAction
    data class OnSearchQueryChange(val query : String):TaskListAction
    data class OnTaskClick(val task : Task):TaskListAction
}