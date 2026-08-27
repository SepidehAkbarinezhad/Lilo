package com.sepideh.lilo.app.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class AppRoutes {

    @Serializable
    data object Home : AppRoutes()

    @Serializable
    data object Settings : AppRoutes()


    @Serializable
    sealed class Tasks : AppRoutes() {
        @Serializable
        data object List : AppRoutes()
        @Serializable
        data class Detail(val taskId : Long?) : AppRoutes()
    }




}