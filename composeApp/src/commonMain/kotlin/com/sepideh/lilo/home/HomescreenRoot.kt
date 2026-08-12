package com.sepideh.lilo.home

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.sepideh.lilo.home.presentation.model.TaskReportDetail

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
        LazyColumn {
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
                            BaseAction.OnNavigateTo(feature.routeForAdding(featureId = null))
                        )
                    },
                    onCardClick = {  onAction(
                        BaseAction.OnNavigateTo(feature.routeForList())
                    ) },
                    detail = state.reportDetails[feature]
                )
            }
        }
    }

}

@AppPreviews
@Composable
private fun HomeScreenPreview() {
    LiloPreviewWrapper {
        HomeScreenContent(
            featureCardFactory = fakeFeatureCardFactory(),
            state = HomeState( ),
            onAction = {},
        )
    }
}

