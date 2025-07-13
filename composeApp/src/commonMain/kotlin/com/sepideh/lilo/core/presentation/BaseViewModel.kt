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

    open fun onAction(action: BaseAction) {
        when (action) {
            is BaseAction.ShowLoading -> {
                baseUiState.update { it.copy(showLoading = action.show) }
            }

            is BaseAction.OnNavigateTo -> {
                viewModelScope.launch {
                    action.route?.let { baseOneTimeEvents.emit(BaseOneTimeEvents(route = action.route)) }
                        ?: baseOneTimeEvents.emit(BaseOneTimeEvents(navigateBack = true))

                }
            }
        }
    }


    abstract fun onResetState()

}