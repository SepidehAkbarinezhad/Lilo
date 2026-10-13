package com.sepideh.lilo.core.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sepideh.lilo.app.navigation.AppRoutes

@Composable
fun BaseRoot(
    modifier: Modifier = Modifier,
    viewModel: BaseViewModel,
    navigateTo: (AppRoutes) -> Unit,
    onBack: () -> Boolean,
    bodyContainer: @Composable () -> Unit,
    dialogContent: @Composable () -> Unit = {}
) {

    val baseUiState by viewModel.baseUiStateValue.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val currentNavigateTo by rememberUpdatedState(navigateTo)
    val currentOnBack by rememberUpdatedState(onBack)
    LaunchedEffect(viewModel, lifecycle) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.baseOneTimeEvents.collect { event ->
                if (event.navigateBack) currentOnBack()
                event.route?.let(currentNavigateTo)
            }
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            viewModel.onResetState()
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {

        bodyContainer()

        if (baseUiState.showLoading) {
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center),
            )
        }

        dialogContent()

    }
}