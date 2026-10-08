package com.sepideh.lilo.app.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.sepideh.lilo.home.HomescreenRoot
import com.sepideh.lilo.home.presentation.HomeViewModel
import com.sepideh.lilo.note.presentation.detail.NoteDetailViewModel
import com.sepideh.lilo.note.presentation.list.NoteListScreenRoot
import com.sepideh.lilo.note.presentation.list.NoteListViewModel
import com.sepideh.lilo.settings.presentation.SettingsScreenRoot
import com.sepideh.lilo.settings.presentation.SettingsViewModel
import com.sepideh.lilo.splash.SplashScreen
import com.sepideh.lilo.task.presentation.detail.TaskDetailScreenRoot
import com.sepideh.lilo.task.presentation.detail.TaskDetailViewModel
import com.sepideh.lilo.task.presentation.list.TaskListScreenRoot
import com.sepideh.lilo.task.presentation.list.TaskListViewModel
import com.sepideh.lilo.note.presentation.detail.NoteDetailScreenRoot
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NavigationGraph(navHostController: NavHostController) {
    NavHost(navController = navHostController, startDestination = AppRoutes.SplashScreen) {

        val onBackPressed = { navHostController.navigateUp() }
        val onNavigate: (AppRoutes) -> Unit =
            { route ->
                navHostController.navigate(route = route)
            }
        composable<AppRoutes.SplashScreen>(
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            SplashScreen(onNavigateTo = { route ->
                navHostController.navigate(route) {
                    popUpTo<AppRoutes.SplashScreen> { inclusive = true }
                    launchSingleTop = true
                }
            })
        }

        composable<AppRoutes.Home>(
            enterTransition = {
                if (initialState.destination.hasRoute<AppRoutes.SplashScreen>()) {
                    fadeIn(animationSpec = tween(300))
                } else null
            }
        ) {
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

        composable<AppRoutes.Notes.List> {
            val viewModel = koinViewModel< NoteListViewModel>()
            NoteListScreenRoot(
                viewModel = viewModel,
                onNavigateTo = onNavigate,
                onBack = onBackPressed
            )
        }

        composable<AppRoutes.Notes.Detail> {
            val args = it.toRoute<AppRoutes.Notes.Detail>()
            val viewModel = koinViewModel<NoteDetailViewModel>()
            NoteDetailScreenRoot(

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

