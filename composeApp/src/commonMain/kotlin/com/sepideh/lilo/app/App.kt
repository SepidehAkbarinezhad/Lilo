package com.sepideh.lilo.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.sepideh.lilo.app.navigation.NavigationGraph
import com.sepideh.lilo.ui.theme.LiloTheme
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun App(
    darkTheme: Boolean,
    dynamicColor: Boolean
) {
    LiloTheme(darkTheme = darkTheme, dynamicColor = dynamicColor){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
                .padding(WindowInsets.systemBars.asPaddingValues())
        ) {
            val navHostController = rememberNavController()
            NavigationGraph(navHostController = navHostController)
        }
    }
}