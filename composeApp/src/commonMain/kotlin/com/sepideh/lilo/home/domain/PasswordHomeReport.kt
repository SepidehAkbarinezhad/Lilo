package com.sepideh.lilo.home.domain

import com.sepideh.lilo.home.presentation.model.PasswordReportDetail
import com.sepideh.lilo.password.domain.Password

internal fun passwordHomeReport(records: List<Password>): PasswordReportDetail {
    val latest = records.maxWithOrNull(compareBy<Password> { it.createdAt }.thenBy { it.id })
    return PasswordReportDetail(records.size, latest?.serviceName.orEmpty(),
        latest?.username?.takeIf { it.isNotBlank() } ?: latest?.title.orEmpty(), records.size,
        latest?.serviceId, latest?.createdAt, latest?.serviceNameFa.orEmpty(), latest?.id?.takeIf { it.isNotBlank() })
}
