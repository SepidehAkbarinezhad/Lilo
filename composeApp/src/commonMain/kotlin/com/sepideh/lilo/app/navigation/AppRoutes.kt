package com.sepideh.lilo.app.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class AppRoutes {
    @Serializable
    data object Splash : AppRoutes()

    @Serializable
    data object TaskList : AppRoutes()

    @Serializable
    data object TaskDetail : AppRoutes()

}