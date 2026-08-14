package com.sepideh.lilo.home.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppCircleButton
import com.sepideh.lilo.core.presentation.components.AppPreviews
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.core.presentation.components.LiloPreviewWrapper
import com.sepideh.lilo.home.domain.FeatureCardFactory
import com.sepideh.lilo.home.domain.fakeFeatureCardFactory
import com.sepideh.lilo.home.presentation.model.LiloFeature
import com.sepideh.lilo.home.presentation.model.ReportDetail
import com.sepideh.lilo.home.presentation.model.TaskReportDetail
import com.sepideh.lilo.ui.theme.LiloColors
import com.sepideh.lilo.ui.theme.LiloExtendedTheme
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun FeatureCardShell(
    featureCardFactory: FeatureCardFactory,
    feature: LiloFeature,
    onAddClick: () -> Unit,
    onCardClick: () -> Unit,
    detail: ReportDetail?
) {
    val colors: LiloColors = LiloExtendedTheme.colors
    val renderStrategy = remember(feature) {
        featureCardFactory.cardFor(feature).getReportRender()
    }
    with(feature) {
        Card(
            modifier = Modifier.padding(12.dp).clickable { onCardClick() },
            colors = CardDefaults.cardColors(
                containerColor = accentColor(colors).copy(alpha = 0.12f)
            )
        ) {

            Row(
                Modifier.fillMaxWidth().padding(12.dp),

                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = feature.accentColor(colors)
                    )
                ) {
                    Icon(
                        modifier = Modifier.padding(12.dp).size(24.dp),
                        imageVector = feature.iconRes,
                        tint = Color.White,
                        contentDescription = ""
                    )

                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    AppText(text = stringResource(feature.titleRes), textType = TextType.Title)
                    detail?.subTitleReportCount?.let {
                        AppText(
                            text = stringResource(
                                feature.subTitleRes,
                                detail.subTitleReportCount
                            ), textType = TextType.SubTitle,
                            color = colors.subtitleText
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                AppCircleButton(color = feature.accentColor(colors), onClick = onAddClick)
            }
            detail?.let { detail ->
                renderStrategy.Render(detail)
            }
        }

    }
}

@AppPreviews
@Composable
private fun HomeScreenPreview() {
    LiloPreviewWrapper {
        FeatureCardShell(
            featureCardFactory = fakeFeatureCardFactory(),
            feature = LiloFeature.TASKS,
            onAddClick = {},
            onCardClick = {},
            detail = TaskReportDetail(
                nextTaskTitle = "",
                nextTaskTime = "",
                remainingCount = 12,
                subTitleReportCount = 2
            )
        )
    }
}

