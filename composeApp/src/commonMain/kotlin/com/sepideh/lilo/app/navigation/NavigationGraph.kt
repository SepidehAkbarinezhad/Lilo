package com.sepideh.lilo.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.sepideh.lilo.app.SplashScreen
import com.sepideh.lilo.task.presentation.task_detail.TaskDetailScreenRoot
import com.sepideh.lilo.task.presentation.task_detail.TaskDetailViewModel
import com.sepideh.lilo.task.presentation.task_list.TaskListScreenRoot
import com.sepideh.lilo.task.presentation.task_list.TaskListViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NavigationGraph(navHostController: NavHostController) {
    NavHost(navController = navHostController, startDestination = AppRoutes.TaskList) {

        val onBackPressed = { navHostController.navigateUp() }
        val onNavigate: (AppDestinations) -> Unit =
            { destination ->
                when (destination) {
                    AppDestinations.NavigateUp() -> onBackPressed()
                    else -> destination.route?.let { route -> navHostController.navigate(route = route) }
                }
            }

        composable<AppRoutes.Splash> {
            SplashScreen(onNavigateTo = onNavigate)
        }

        composable<AppRoutes.TaskList> {
            val viewModel = koinViewModel<TaskListViewModel>()
            TaskListScreenRoot(viewModel = viewModel, onNavigateTo = onNavigate)
        }

        composable<AppRoutes.TaskDetail> {
            val args = it.toRoute<AppRoutes.TaskDetail>()
            val viewModel = koinViewModel<TaskDetailViewModel>()
            TaskDetailScreenRoot(
                taskId = args.taskId,
                viewModel = viewModel,
                onNavigateTo = onNavigate
            )
        }

    }
}

