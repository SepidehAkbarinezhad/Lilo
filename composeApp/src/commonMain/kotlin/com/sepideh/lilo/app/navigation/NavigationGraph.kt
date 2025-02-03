package com.sepideh.lilo.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sepideh.lilo.app.SplashScreen
import com.sepideh.lilo.task.presentation.task_list.TaskListScreenRoot
import com.sepideh.lilo.task.presentation.task_list.TaskListViewModel

@Composable
fun NavigationGraph(navHostController: NavHostController) {
    NavHost(navController = navHostController, startDestination = AppRoutes.TaskList) {

        val onBackPressed = { navHostController.navigateUp() }
        val onNavigate: (AppDestinations) -> Unit =
            { destination ->
                when (destination) {
                    AppDestinations.NavigateUp() -> onBackPressed()
                    else -> navHostController.navigate(route = destination) {
                        if (destination is AppDestinations.TaskList) {
                            popUpTo(navHostController.graph.startDestinationId) { inclusive = true }
                        }
                    }
                }
            }

        composable<AppRoutes.Splash> {
            SplashScreen(onNavigateTo = onNavigate)
        }

        composable<AppRoutes.TaskList> {
            TaskListScreenRoot(viewModel = TaskListViewModel(), onNavigateTo = onNavigate)
        }
    }
}