package com.sepideh.lilo.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.compose.rememberNavController
import com.sepideh.lilo.app.navigation.NavigationGraph
import com.sepideh.lilo.ui.theme.LiloTheme


@Composable
fun App(
    darkTheme: Boolean,
    dynamicColor: Boolean
) {
    LiloTheme(darkTheme = darkTheme, dynamicColor = dynamicColor) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            val navHostController = rememberNavController()
            NavigationGraph(navHostController = navHostController)
        }
    }
}