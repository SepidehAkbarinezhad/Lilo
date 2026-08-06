package com.sepideh.lilo.home.presentation

import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.home.presentation.model.LiloFeature

sealed interface HomeAction : BaseAction {
    data class ObserveFeature(val feature: LiloFeature) : HomeAction
}