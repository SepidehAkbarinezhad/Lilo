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
import com.sepideh.lilo.app.navigation.AppDestinations

@Composable
fun BaseRoot(
    viewModel: BaseViewModel,
    navigateTo: (AppDestinations) -> Unit,
    bodyContainer: @Composable () -> Unit
) {
    val baseOneTimeEvents by viewModel.baseOneTimeEvents.collectAsStateWithLifecycle(BaseOneTimeEvents())
    val loading by viewModel.loadingValue.collectAsStateWithLifecycle()

    LaunchedEffect(loading){
        println("loading : $loading")
    }
    LaunchedEffect(baseOneTimeEvents) {
        baseOneTimeEvents.navigateTo?.let {
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
        if (loading){
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center),
            )
        }


    }
}