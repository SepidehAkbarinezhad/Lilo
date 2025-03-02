package com.sepideh.lilo.core.presentation

import com.sepideh.lilo.app.navigation.AppDestinations

data class BaseUiState(
    val navigateTo: AppDestinations? = null
)
