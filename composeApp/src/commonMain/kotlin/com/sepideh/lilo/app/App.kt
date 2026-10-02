package com.sepideh.lilo.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.rememberNavController
import com.sepideh.lilo.app.navigation.NavigationGraph
import com.sepideh.lilo.ui.theme.LiloTheme


@Composable
fun App() {

    CompositionLocalProvider() {
        LiloTheme {
            com.sepideh.lilo.task.presentation.reminder.ReminderLifecycleObserver()
            val navHostController = rememberNavController()
            NavigationGraph(navHostController = navHostController)
        }
    }
}


