package com.sepideh.lilo.app.navigation

import com.sepideh.lilo.home.presentation.model.LiloFeature

fun LiloFeature.routeForList(): AppRoutes =
    when(this) {
        LiloFeature.TASKS -> AppRoutes.Tasks.List
        LiloFeature.NOTES -> AppRoutes.Notes.List
        LiloFeature.PASSWORDS -> AppRoutes.Passwords.List
    }


fun LiloFeature.routeForAdding(itemId : Long?): AppRoutes =
    when(this) {
        LiloFeature.TASKS -> AppRoutes.Tasks.Detail(itemId)
        LiloFeature.NOTES -> AppRoutes.Notes.Detail(itemId)
        LiloFeature.PASSWORDS -> AppRoutes.Passwords.Detail(null)
    }