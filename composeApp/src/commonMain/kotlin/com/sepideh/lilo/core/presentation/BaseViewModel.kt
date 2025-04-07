package com.sepideh.lilo.core.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    val baseUiState = MutableSharedFlow<BaseUiState>()

    open fun onEvent(event: BaseEvent) {
        when (event) {
            is BaseEvent.OnNavigateTo -> {
                viewModelScope.launch {
                    baseUiState.emit(BaseUiState(navigateTo = event.destination))
                }
            }
        }
    }


     abstract fun onResetState()

}