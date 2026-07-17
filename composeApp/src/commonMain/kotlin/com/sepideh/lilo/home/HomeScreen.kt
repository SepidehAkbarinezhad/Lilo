package com.sepideh.lilo.home

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sepideh.lilo.app.navigation.AppRoutes
import com.sepideh.lilo.core.presentation.BaseRoot
import com.sepideh.lilo.home.domain.FeatureCardFactory
import com.sepideh.lilo.home.domain.fakeFeatureCardFactory
import com.sepideh.lilo.home.presentation.HomeAction
import com.sepideh.lilo.home.presentation.HomeState
import com.sepideh.lilo.home.presentation.HomeViewModel
import com.sepideh.lilo.home.presentation.components.FeatureCardShell
import com.sepideh.lilo.home.presentation.model.LiloFeature
import com.sepideh.lilo.settings.presentation.SettingsScreen
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import kotlin.collections.forEach

@Composable
fun Homescreen(
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
                onAction = viewModel::onAction
            )
        }
    )
}

@Composable
fun HomeScreenContent(
    state: HomeState,
    onAction: (HomeAction) -> Unit,
    featureCardFactory: FeatureCardFactory = koinInject()
) {
    LazyColumn {
        items(LiloFeature.entries, key = { it.name }) { feature ->

            // Each card triggers its own data subscription once it enters composition
            LaunchedEffect(feature) {
                onAction(HomeAction.ObserveFeature(feature))
            }

            val renderStrategy = remember(feature) {
                featureCardFactory.cardFor(feature).getReportRender()
            }

            FeatureCardShell(
                feature = feature,
                onAddClick = { onAction(HomeAction.AddClicked(feature)) },
                onMoreClick = { onAction(HomeAction.MoreClicked(feature)) },
                onCardClick = { onAction(HomeAction.FeatureCardClicked(feature)) }
            ) {
                state.reportDetails[feature]?.let { detail ->
                    renderStrategy.Render(detail)
                }
            }
        }
    }
}


@Preview
@Composable
fun HomescreenContentPrev() {
    HomeScreenContent(
        state = HomeState(), onAction = {}, featureCardFactory = fakeFeatureCardFactory()
    )
}