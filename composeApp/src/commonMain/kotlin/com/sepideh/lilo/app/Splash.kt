package com.sepideh.lilo.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sepideh.lilo.app.navigation.AppDestinations
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.core.presentation.components.TextType

@Composable
fun SplashScreen(onNavigateTo: (AppDestinations) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        AppText(text = "lilo", textType = TextType.SubTitle)
    }
}