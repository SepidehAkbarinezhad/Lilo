package com.sepideh.lilo.home.presentation.model

sealed interface ReportDetail {
    val subTitleReportCount: Int
}

data class TaskReportDetail(
    val nextTaskTitle: String?,
    val nextTaskTime: String?,
    val remainingCount: Int,
    override val subTitleReportCount: Int,
) : ReportDetail

data class NoteReportDetail(
    val latestTitle: String,
    val latestSnippet: String,
    val totalCount: Int,
    val thumbnailUrl: String?,
    override val subTitleReportCount: Int
) : ReportDetail

data class ExpenseReportDetail(
    val totalThisMonth: Double,
    val lastCategory: String,
    val lastAmount: Double,
    override val subTitleReportCount: Int
) : ReportDetail

data class PasswordReportDetail(
    val totalAccounts: Int,
    val lastAddedService: String,
    val lastAddedEmail: String,
    override val subTitleReportCount: Int
) : ReportDetail