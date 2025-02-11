package com.sepideh.lilo.app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.sepideh.lilo.app.navigation.NavigationGraph
import com.sepideh.lilo.ui.theme.LiloTheme
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun App(
    darkTheme: Boolean,
    dynamicColor: Boolean
) {
    LiloTheme(darkTheme = darkTheme, dynamicColor = dynamicColor){
        val navHostController = rememberNavController()
        NavigationGraph(navHostController = navHostController)
    }
}