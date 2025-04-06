package com.sepideh.lilo.core.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class BaseViewModel : ViewModel() {

    protected val baseUiState = MutableStateFlow(BaseUiState())
    val baseUiStateValue = baseUiState.asStateFlow()

    open fun onEvent(event: BaseEvent) {
        when (event) {
            is BaseEvent.OnNavigateTo -> {
                baseUiState.update { it.copy(navigateTo = event.destination) }
            }
        }
    }

    fun resetState() {
        resetBaseUiState()
        onResetState()
    }

    private fun resetBaseUiState() {
        baseUiState.value = BaseUiState()
    }

    protected abstract fun onResetState()

}