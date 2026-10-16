package com.sepideh.lilo.home.presentation.model

sealed interface ReportDetail {
    val subTitleReportCount: Int
}

data class TaskReportDetail(
    val nextTaskTitle: String?,
    val nextTaskTime: String?,
    val remainingCount: Int,
    override val subTitleReportCount: Int,
    val nextTaskId: Long? = null,
    val nextTaskPriority: Int? = null,
    val nextTaskDone: Boolean = false,
    val nextTaskDescription: String = "",
) : ReportDetail {
    val completedCount: Int get() = (subTitleReportCount - remainingCount).coerceAtLeast(0)
}

data class NoteReportDetail(
    val latestTitle: String,
    val latestSnippet: String,
    val totalCount: Int,
    val thumbnailUrl: String?,
    override val subTitleReportCount: Int,
    val latestUpdatedAt: Long? = null,
    val latestNoteId: Long? = null,
) : ReportDetail

data class ExpenseReportDetail(
    val totalThisMonth: Double,
    val lastGroup: String,
    val lastAmount: Double,
    override val subTitleReportCount: Int
) : ReportDetail

data class PasswordReportDetail(
    val totalAccounts: Int,
    val lastAddedService: String,
    val lastAddedIdentifier: String,
    override val subTitleReportCount: Int,
    val lastAddedServiceId: String? = null,
    val lastAddedAt: Long? = null,
    val lastAddedServiceFa: String = lastAddedService,
    val lastAddedPasswordId: String? = null,
) : ReportDetail