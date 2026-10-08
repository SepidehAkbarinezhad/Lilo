package com.sepideh.lilo.home

import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import lilo.composeapp.generated.resources.task_operation_error
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sepideh.lilo.app.navigation.AppRoutes
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.BaseRoot
import com.sepideh.lilo.core.presentation.BaseScreen
import com.sepideh.lilo.core.presentation.components.AppPreviews
import com.sepideh.lilo.core.presentation.components.LiloPreviewWrapper
import com.sepideh.lilo.home.domain.FeatureCardFactory
import com.sepideh.lilo.home.domain.fakeFeatureCardFactory
import com.sepideh.lilo.home.presentation.HomeAction
import com.sepideh.lilo.home.presentation.HomeState
import com.sepideh.lilo.home.presentation.HomeViewModel
import com.sepideh.lilo.home.presentation.components.FeatureCardShell
import com.sepideh.lilo.home.presentation.components.HomeHeader
import com.sepideh.lilo.home.presentation.model.LiloFeature
import org.koin.compose.koinInject
import com.sepideh.lilo.app.navigation.routeForAdding
import com.sepideh.lilo.app.navigation.routeForList

@Composable
fun HomescreenRoot(
    viewModel: HomeViewModel,
    onNavigateTo: (AppRoutes) -> Unit,
    onBack: () -> Boolean
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    BaseRoot(
        viewModel = viewModel,
        navigateTo = onNavigateTo,
        onBack = onBack,
        bodyContainer = {
            HomeScreenContent(
                state = state,
                onAction = viewModel::onAction,
            )
        }
    )
}

@Composable
fun HomeScreenContent(
    featureCardFactory: FeatureCardFactory = koinInject(),
    state: HomeState,
    onAction: (BaseAction) -> Unit,
) {
    BaseScreen(
        header = {
            HomeHeader(onAction = onAction)
        }
    ) {
        if (state.completionFailed) com.sepideh.lilo.core.presentation.components.AppText(
            text = lilo.composeapp.generated.resources.Res.string.task_operation_error,
            color = androidx.compose.material3.MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp))
        LazyColumn(Modifier.fillMaxSize().navigationBarsPadding(), contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 26.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(LiloFeature.entries, key = { it.name }) { feature ->

                // Each card triggers its own data subscription once it enters composition
                LaunchedEffect(feature) {
                    onAction(HomeAction.ObserveFeature(feature))
                }

                FeatureCardShell(
                    featureCardFactory = featureCardFactory,
                    feature = feature,
                    onAddClick = {
                        onAction(
                            BaseAction.OnNavigateTo(feature.routeForAdding(itemId = null))
                        )
                    },
                    onCardClick = {  onAction(
                        BaseAction.OnNavigateTo(feature.routeForList())
                    ) },
                    onCompleteTask = { onAction(HomeAction.CompleteTask(it)) },
                    completingTaskIds = state.completingTaskIds,
                    detail = state.reportDetails[feature]
                )
            }
        }
    }

}

@AppPreviews
@Composable
private fun HomePreview() {
    LiloPreviewWrapper {
        HomeScreenContent(
            featureCardFactory = fakeFeatureCardFactory(),
            state = HomeState( ),
            onAction = {},
        )
    }
}

