package com.sepideh.lilo.core.presentation

import com.sepideh.lilo.app.navigation.AppDestinations

interface BaseEvent {
    data class OnNavigateTo(val destination: AppDestinations) : BaseEvent
    data class SetLoading(val isLoading : Boolean) : BaseEvent
}
