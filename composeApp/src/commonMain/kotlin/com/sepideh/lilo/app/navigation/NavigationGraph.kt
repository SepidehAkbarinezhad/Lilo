package com.sepideh.lilo.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.sepideh.lilo.home.HomescreenRoot
import com.sepideh.lilo.home.presentation.HomeViewModel
import com.sepideh.lilo.settings.presentation.SettingsScreenRoot
import com.sepideh.lilo.settings.presentation.SettingsViewModel
import com.sepideh.lilo.task.presentation.task_detail.TaskDetailScreenRoot
import com.sepideh.lilo.task.presentation.task_detail.TaskDetailViewModel
import com.sepideh.lilo.task.presentation.task_list.TaskListScreenRoot
import com.sepideh.lilo.task.presentation.task_list.TaskListViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NavigationGraph(navHostController: NavHostController) {
    NavHost(navController = navHostController, startDestination = AppRoutes.Home) {

        val onBackPressed = { navHostController.navigateUp() }
        val onNavigate: (AppRoutes) -> Unit =
            { route ->
                navHostController.navigate(route = route)
            }

        composable<AppRoutes.Home> {
            val viewModel = koinViewModel<HomeViewModel>()
            HomescreenRoot(viewModel = viewModel, onNavigateTo = onNavigate, onBack = onBackPressed)
        }

        composable<AppRoutes.Tasks.List> {
            val viewModel = koinViewModel<TaskListViewModel>()
            TaskListScreenRoot(
                viewModel = viewModel,
                onNavigateTo = onNavigate,
                onBack = onBackPressed
            )
        }

        composable<AppRoutes.Tasks.Detail> {
            val args = it.toRoute<AppRoutes.Tasks.Detail>()
            val viewModel = koinViewModel<TaskDetailViewModel>()
            TaskDetailScreenRoot(
                taskId = args.taskId,
                viewModel = viewModel,
                onNavigateTo = onNavigate,
                onBack = onBackPressed
            )
        }

        composable<AppRoutes.Settings> {
            val viewModel = koinViewModel<SettingsViewModel>()
            SettingsScreenRoot(
                viewModel = viewModel,
                onNavigateTo = onNavigate,
                onBack = onBackPressed
            )
        }


    }
}

