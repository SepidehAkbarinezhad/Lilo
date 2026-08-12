package com.sepideh.lilo.home.presentation

import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.home.presentation.model.LiloFeature
import com.sepideh.lilo.task.presentation.task_list.TaskListAction

sealed interface HomeAction : BaseAction {
    data class ObserveFeature(val feature: LiloFeature) : HomeAction

}