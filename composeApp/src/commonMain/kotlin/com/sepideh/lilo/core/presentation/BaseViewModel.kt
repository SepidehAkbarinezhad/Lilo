package com.sepideh.lilo.core.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    // Keep navigation pending while the screen lifecycle has no active collector.
    private val events = Channel<BaseOneTimeEvents>(Channel.BUFFERED)
    val baseOneTimeEvents = events.receiveAsFlow()

    private val baseUiState = MutableStateFlow(BaseUiState())
    val baseUiStateValue = baseUiState.asStateFlow()

    open fun onAction(action: BaseAction) {
        when (action) {
            is BaseAction.ShowLoading -> {
                baseUiState.update { it.copy(showLoading = action.show) }
            }

            is BaseAction.OnNavigateTo -> {
                viewModelScope.launch {
                    action.route?.let { events.send(BaseOneTimeEvents(route = action.route)) }
                        ?: events.send(BaseOneTimeEvents(navigateBack = true))

                }
            }
        }
    }


    abstract fun onResetState()

}