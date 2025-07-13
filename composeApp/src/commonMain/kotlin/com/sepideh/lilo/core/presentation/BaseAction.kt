package com.sepideh.lilo.core.presentation

import com.sepideh.lilo.app.navigation.AppRoutes


interface BaseAction {
    data class OnNavigateTo(val route: AppRoutes?) : BaseAction
    data class ShowLoading(val show: Boolean) : BaseAction
}
