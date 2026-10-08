package com.sepideh.lilo.home.presentation

import com.sepideh.lilo.home.presentation.model.LiloFeature
import com.sepideh.lilo.home.presentation.model.ReportDetail

data class HomeState(
    val completingTaskIds: Set<Long> = emptySet(),
    val completionFailed: Boolean = false,
    val reportDetails: Map<LiloFeature, ReportDetail?> =
        LiloFeature.entries.associateWith { null }
)
