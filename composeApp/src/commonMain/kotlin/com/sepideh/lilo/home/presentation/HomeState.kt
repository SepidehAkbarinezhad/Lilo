package com.sepideh.lilo.home.presentation

import com.sepideh.lilo.home.presentation.model.LiloFeature
import com.sepideh.lilo.home.presentation.model.ReportDetail

data class HomeState(
    val failedFeatures: Set<LiloFeature> = emptySet(),
    val completingTaskIds: Set<Long> = emptySet(),
    val completionFailed: Boolean = false,
    val openingPasswordId: String? = null,
    val passwordRequest: com.sepideh.lilo.password.domain.crypto.PasswordUnlockRequest? = null,
    val passwordAuthError: com.sepideh.lilo.password.domain.crypto.UnlockResult? = null,
    val passwordOpenFailed: Boolean = false,
    val reportDetails: Map<LiloFeature, ReportDetail?> =
        LiloFeature.entries.associateWith { null }
)
