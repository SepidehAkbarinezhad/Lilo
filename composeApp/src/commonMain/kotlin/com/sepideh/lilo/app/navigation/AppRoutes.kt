package com.sepideh.lilo.app.navigation

import kotlinx.serialization.Serializable

interface AppRoutes {
    @Serializable
    data object Splash : AppRoutes

    @Serializable
    data object TaskList : AppRoutes
}