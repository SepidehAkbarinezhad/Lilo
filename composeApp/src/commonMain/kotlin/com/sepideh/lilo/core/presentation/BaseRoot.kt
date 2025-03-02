package com.sepideh.lilo.core.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sepideh.lilo.app.navigation.AppDestinations

@Composable
fun BaseRoot(
    viewModel: BaseViewModel,
    navigateTo: (AppDestinations) -> Unit,
    bodyContainer: @Composable () -> Unit
) {
    val baseUiState by viewModel.baseUiStateValue.collectAsStateWithLifecycle()
    LaunchedEffect(baseUiState) {
        baseUiState.navigateTo?.let {
            navigateTo(it)
        }
    }
    bodyContainer()
}