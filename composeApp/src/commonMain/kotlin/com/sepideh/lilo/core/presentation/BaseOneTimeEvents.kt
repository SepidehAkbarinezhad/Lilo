package com.sepideh.lilo.core.presentation

import com.sepideh.lilo.app.navigation.AppRoutes


data class BaseOneTimeEvents(
    val navigateBack: Boolean = false,
    val route: AppRoutes? = null,
)
