package com.sepideh.lilo.home.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.*
import com.sepideh.lilo.core.presentation.icons.LiloIcons
import com.sepideh.lilo.core.presentation.format.localizedDigits
import com.sepideh.lilo.home.domain.FeatureCardFactory
import com.sepideh.lilo.home.presentation.model.*
import com.sepideh.lilo.ui.theme.LiloExtendedTheme
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

/** Stacked cards grow independently as each feature becomes available. */
@Composable
fun FeatureCardShell(
    featureCardFactory: FeatureCardFactory,
    feature: LiloFeature,
    onAddClick: () -> Unit,
    onCardClick: () -> Unit,
    detail: ReportDetail?
) {
    val colors = LiloExtendedTheme.colors
    val accent = feature.accentColor(colors)
    val dark = MaterialTheme.colorScheme.background.luminance() < .5f
    val foreground = if (dark) accent else lerp(accent, colors.title, .55f)
    val cardColor = lerp(MaterialTheme.colorScheme.background, accent, if (dark) .13f else .20f)
    val title = stringResource(feature.titleRes)
    val persian = LocalLayoutDirection.current == LayoutDirection.Rtl

    Surface(onClick = onCardClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp),
        color = cardColor, contentColor = colors.textPrimary) {
        Column(Modifier.padding(horizontal = 18.dp, vertical = 14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(14.dp), color = accent.copy(alpha = if (dark) .15f else .30f)) {
                    Box(Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                        Icon(if (feature == LiloFeature.NOTES) org.jetbrains.compose.resources.vectorResource(Res.drawable.home_note) else feature.iconRes,
                            null, Modifier.size(22.dp), tint = foreground)
                    }
                }
                Spacer(Modifier.width(10.dp))
                AppText(text = title, textType = TextType.SectionTitle, maxLines = 1, modifier = Modifier.weight(1f))
                IconButton(onClick = onAddClick) {
                    Icon(LiloIcons.Add, stringResource(Res.string.home_add_feature, title), Modifier.size(24.dp), tint = foreground)
                }
            }
            when (detail) {
                is TaskReportDetail -> {
                    AppText(text = stringResource(Res.string.home_tasks_remaining, detail.remainingCount).localizedDigits(persian),
                        textType = TextType.Caption, color = colors.textSecondary)
                    if (detail.nextTaskTitle.isNullOrBlank()) {
                        HomeEmptyPreview(if (detail.subTitleReportCount == 0) Res.string.home_no_tasks else Res.string.home_tasks_done)
                    } else {
                        Row(Modifier.fillMaxWidth().heightIn(min = 48.dp), verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(11.dp)) {
                            Surface(shape = RoundedCornerShape(7.dp), color = androidx.compose.ui.graphics.Color.Transparent,
                                border = BorderStroke(1.4.dp, foreground)) { Spacer(Modifier.size(21.dp)) }
                            AppText(text = detail.nextTaskTitle, maxLines = 1, modifier = Modifier.weight(1f))
                        }
                    }
                }
                is NoteReportDetail -> {
                    AppText(text = Res.string.home_recent, textType = TextType.Caption, color = colors.textSecondary)
                    if (detail.totalCount == 0) HomeEmptyPreview(Res.string.home_no_notes)
                    else Column(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        AppText(text = detail.latestTitle, maxLines = 1)
                        if (detail.latestSnippet.isNotBlank()) AppText(text = detail.latestSnippet.replace('\n', ' '),
                            textType = TextType.Caption, color = colors.textSecondary, maxLines = 2)
                    }
                }
                null -> LinearProgressIndicator(Modifier.fillMaxWidth().padding(vertical = 12.dp), color = foreground, trackColor = accent.copy(alpha = .15f))
                else -> Unit
            }
        }
    }
}

@Composable
private fun HomeEmptyPreview(message: org.jetbrains.compose.resources.StringResource) {
    AppText(text = message, color = LiloExtendedTheme.colors.textSecondary, modifier = Modifier.padding(vertical = 12.dp))
}

@AppPreviews
@Composable
private fun PopulatedHomeCardPreview() {
    LiloPreviewWrapper {
        FeatureCardShell(com.sepideh.lilo.home.domain.fakeFeatureCardFactory(), LiloFeature.TASKS, {}, {},
            TaskReportDetail("Pick up a parcel", null, 3, 3))
    }
}

@AppPreviews
@Composable
private fun EmptyHomeCardPreview() {
    LiloPreviewWrapper {
        FeatureCardShell(com.sepideh.lilo.home.domain.fakeFeatureCardFactory(), LiloFeature.NOTES, {}, {},
            NoteReportDetail("", "", 0, null, 0))
    }
}
