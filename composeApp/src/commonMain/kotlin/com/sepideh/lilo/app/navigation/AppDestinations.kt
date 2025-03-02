package com.sepideh.lilo.app.navigation

import kotlinx.serialization.Serializable

sealed interface AppDestinations {
    val route: AppRoutes?

    @Serializable
    data class NavigateUp(override val route: AppRoutes? = null) : AppDestinations
    data class Splash(override val route: AppRoutes = AppRoutes.Splash) : AppDestinations
    @Serializable
    data class TaskList(override val route: AppRoutes = AppRoutes.TaskList) : AppDestinations
    @Serializable
    data class TaskDetail(override val route: AppRoutes = AppRoutes.TaskDetail) : AppDestinations
}