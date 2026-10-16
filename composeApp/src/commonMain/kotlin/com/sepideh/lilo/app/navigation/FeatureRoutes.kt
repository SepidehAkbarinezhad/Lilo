package com.sepideh.lilo.app.navigation

import com.sepideh.lilo.home.presentation.model.*

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

/** Null IDs represent empty/unavailable previews and must never open a new-item form. */
fun ReportDetail.detailRoute(): AppRoutes? = when (this) {
    is TaskReportDetail -> nextTaskId?.let { AppRoutes.Tasks.Detail(it) }
    is NoteReportDetail -> latestNoteId?.let { AppRoutes.Notes.Detail(it) }
    is PasswordReportDetail -> lastAddedPasswordId?.takeIf { it.isNotBlank() }?.let { AppRoutes.Passwords.Detail(it) }
    else -> null
}
