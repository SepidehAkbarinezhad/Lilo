package com.sepideh.lilo.core.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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

    val oneTimeEvents by viewModel.baseOneTimeEvents.collectAsStateWithLifecycle(
        BaseOneTimeEvents()
    )
    val baseUiState by viewModel.baseUiStateValue.collectAsStateWithLifecycle()

    LaunchedEffect(oneTimeEvents) {
        if(oneTimeEvents.navigateBack)
            onBack()
        oneTimeEvents.route?.let {
            navigateTo(it)
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