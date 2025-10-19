package com.sepideh.lilo.app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.sepideh.lilo.app.navigation.NavigationGraph
import com.sepideh.lilo.ui.theme.LiloTheme


@Composable
fun App() {
    LiloTheme {

        val navHostController = rememberNavController()
        NavigationGraph(navHostController = navHostController)
    }

}