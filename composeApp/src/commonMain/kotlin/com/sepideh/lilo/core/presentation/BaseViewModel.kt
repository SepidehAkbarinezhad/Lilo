package com.sepideh.lilo.core.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    val baseOneTimeEvents = MutableSharedFlow<BaseOneTimeEvents>()

    private val baseUiState = MutableStateFlow(BaseUiState())
    val baseUiStateValue = baseUiState.asStateFlow()

    open fun onEvent(event: BaseEvent) {
        when (event) {
            is BaseEvent.ShowLoading -> {
                println("Setting loading to ${event.show}")
                baseUiState.update { it.copy(showLoading = event.show) }
            }

            is BaseEvent.OnNavigateTo -> {
                viewModelScope.launch {
                    baseOneTimeEvents.emit(BaseOneTimeEvents(navigateTo = event.destination))
                }
            }
        }
    }


    abstract fun onResetState()

}