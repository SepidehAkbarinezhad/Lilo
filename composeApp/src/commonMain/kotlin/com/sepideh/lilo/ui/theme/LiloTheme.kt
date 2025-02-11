package com.sepideh.lilo.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

val DarkColorScheme = darkColorScheme(
    surface = AmberSurfaceDark
)
val LightColorScheme = lightColorScheme(
    primary = AmberPrimaryLight,
    onPrimary = OnAmberPrimaryLight,
    secondary = AmberSecondaryLight,
    onSecondary = OnAmberPrimaryLight,
    surface = AmberSurfaceLight
)

@Composable
expect fun LiloTheme(
    darkTheme : Boolean,
    dynamicColor : Boolean,
    content : @Composable ()->Unit
)