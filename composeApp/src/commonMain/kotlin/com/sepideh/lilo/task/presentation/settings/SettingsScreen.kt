package com.sepideh.lilo.task.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.app.navigation.AppRoutes
import com.sepideh.lilo.core.presentation.BaseRoot

@Composable
fun SettingsScreenRoot(
    viewModel: SettingsViewModel,
    onNavigateTo: (AppRoutes) -> Unit,
    onBack: () -> Boolean
) {

    BaseRoot(
        viewModel = viewModel,
        navigateTo = onNavigateTo,
        onBack = onBack,
        bodyContainer = {

        }
    )

}

@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {

        }
        Surface(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {

            }
        }

    }

}
