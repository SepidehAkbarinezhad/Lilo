package com.sepideh.lilo.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.navigation.compose.rememberNavController
import com.sepideh.lilo.app.navigation.NavigationGraph
import com.sepideh.lilo.password.presentation.PasswordSessionEffect
import com.sepideh.lilo.task.presentation.reminder.ReminderLifecycleObserver
import com.sepideh.lilo.ui.theme.LiloTheme


@Composable
fun App() {

    val navHostController = rememberNavController()
    val savedState = rememberSaveableStateHolder()
    LiloTheme {
        PasswordSessionEffect()
        ReminderLifecycleObserver()
        savedState.SaveableStateProvider("localized-app") {
            NavigationGraph(navHostController = navHostController)
        }
    }
}


