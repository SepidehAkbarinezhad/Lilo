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

    private val loading = MutableStateFlow(false)
    val loadingValue = loading.asStateFlow()

    open fun onEvent(event: BaseEvent) {
        when (event) {
            is BaseEvent.SetLoading -> {
                loading.update { !it }
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