package com.sepideh.lilo.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.luminance
import com.sepideh.lilo.ui.theme.DarkBackground
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.MotionDurationScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.app.navigation.AppRoutes
import com.sepideh.lilo.core.presentation.components.AppPreviews
import com.sepideh.lilo.core.presentation.components.LiloPreviewWrapper
import com.sepideh.lilo.core.presentation.components.brand.LiloLogoMorph
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigateTo: (AppRoutes) -> Unit) {
    val progress = remember { Animatable(0f) }
    val navigate by rememberUpdatedState(onNavigateTo)
    LaunchedEffect(Unit) {
        val motionEnabled = (currentCoroutineContext()[MotionDurationScale]?.scaleFactor ?: 1f) > 0f
        if (motionEnabled) {
            progress.animateTo(1f, tween(durationMillis = 1350, easing = LinearEasing))
        } else {
            progress.snapTo(1f)
        }
        navigate(AppRoutes.Home)
    }
    val background = splashBackground()
    Box(Modifier.fillMaxSize().background(background), contentAlignment = Alignment.Center) {
        LiloLogoMorph(progress.value, Modifier.size(width = 240.dp, height = 160.dp), background)
    }
}

@AppPreviews
@Composable
private fun SplashLogoPreview() {
    LiloPreviewWrapper {
        val background = splashBackground()
        Box(Modifier.fillMaxSize().background(background), contentAlignment = Alignment.Center) {
            LiloLogoMorph(0f, Modifier.size(240.dp, 160.dp), background)
        }
    }
}

@AppPreviews
@Composable
private fun SplashWordmarkPreview() {
    LiloPreviewWrapper {
        val background = splashBackground()
        Box(Modifier.fillMaxSize().background(background), contentAlignment = Alignment.Center) {
            LiloLogoMorph(1f, Modifier.size(240.dp, 160.dp), background)
        }
    }
}

@Composable
private fun splashBackground(): Color =
    if (MaterialTheme.colorScheme.background.luminance() < .5f) DarkBackground else Color.White
