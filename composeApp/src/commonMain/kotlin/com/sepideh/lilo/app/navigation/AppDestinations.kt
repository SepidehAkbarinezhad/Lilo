package com.sepideh.lilo.app.navigation

sealed interface AppDestinations {
    val route: AppRoutes?

    data class NavigateUp(override val route: AppRoutes? = null) : AppDestinations
    data class Splash(override val route: AppRoutes = AppRoutes.Splash) : AppDestinations
    data class TaskList(override val route: AppRoutes = AppRoutes.TaskList) : AppDestinations
    data class TaskDetail(override val route: AppRoutes = AppRoutes.TaskDetail) : AppDestinations
}