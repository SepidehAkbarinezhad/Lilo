package com.sepideh.lilo.home.presentation.components

import com.sepideh.lilo.password.presentation.components.localizedServiceName

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.components.*
import com.sepideh.lilo.home.presentation.model.*
import com.sepideh.lilo.ui.theme.LiloExtendedTheme
import com.sepideh.lilo.password.presentation.list.components.PasswordServiceIcon
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

/** Assembles the heading and report; each feature owns its report layout. */
@Composable
fun FeatureCardShell(
    feature: LiloFeature,
    onAddClick: () -> Unit,
    onCardClick: () -> Unit,
    detail: ReportDetail?,
    onCompleteTask: (Long) -> Unit = {},
    completingTaskIds: Set<Long> = emptySet(),
    reportFailed: Boolean = false,
    onRetryReport: () -> Unit = {},
    onLatestClick: (() -> Unit)? = null,
) {
    val colors = LiloExtendedTheme.colors
    val accent = feature.accentColor(colors)
    val dark = MaterialTheme.colorScheme.background.luminance() < .5f
    val actionColor = when (feature) {
        LiloFeature.TASKS -> colors.taskAction
        LiloFeature.NOTES -> colors.noteAction
        LiloFeature.PASSWORDS -> colors.passwordAction
    }
    val backgroundTint = when {
        dark -> .16f
        feature == LiloFeature.PASSWORDS -> .30f
        else -> .24f
    }
    Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp),
        color = lerp(MaterialTheme.colorScheme.background, accent, backgroundTint),
        contentColor = colors.textPrimary) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            HomeFeatureHeading(feature, actionColor, onAddClick, onCardClick)
            HorizontalDivider(color = actionColor.copy(alpha = .14f))
            if (reportFailed) {
                AppText(text = Res.string.home_report_error, color = colors.textSecondary)
                TextButton(onClick = onRetryReport, colors = ButtonDefaults.textButtonColors(contentColor = actionColor)) {
                    Text(stringResource(Res.string.note_retry))
                }
            } else when (detail) {
                is TaskReportDetail -> HomeTaskReport(detail, actionColor, completingTaskIds, onCompleteTask, onCardClick, onLatestClick)
                is NoteReportDetail -> HomeLatestSummary(detail.totalCount, Res.string.home_total_notes,
                    detail.latestTitle, detail.latestUpdatedAt, Res.string.home_no_items, onCardClick, onLatestClick)
                is PasswordReportDetail -> HomeLatestSummary(detail.totalAccounts, Res.string.home_total_passwords,
                    localizedServiceName(detail.lastAddedService, detail.lastAddedServiceFa), detail.lastAddedAt, Res.string.home_no_items, onCardClick, onLatestClick,
                    leadingIcon = { PasswordServiceIcon(detail.lastAddedServiceId, colors.textSecondary, Modifier.size(20.dp)) },
                    subtitle = detail.lastAddedIdentifier)
                null -> LinearProgressIndicator(Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    color = actionColor, trackColor = accent.copy(alpha = .15f))
                else -> Unit
            }
        }
    }
}

@AppPreviews
@Composable
private fun PopulatedHomeCardsPreview() {
    LiloPreviewWrapper {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            FeatureCardShell(LiloFeature.TASKS, {}, {}, TaskReportDetail("Buy a birthday gift", "15:00", 5, 12, 1, 0, nextTaskDescription = "Pick something from the bookshop"))
            FeatureCardShell(LiloFeature.NOTES, {}, {}, NoteReportDetail("Weekend trip", "", 8, null, 8))
            FeatureCardShell(LiloFeature.PASSWORDS, {}, {}, PasswordReportDetail(6, "GitHub", "sepideh", 6, "github"))
        }
    }
}

@AppPreviews
@Composable
private fun EmptyHomeCardsPreview() {
    LiloPreviewWrapper {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            FeatureCardShell(LiloFeature.TASKS, {}, {}, TaskReportDetail(null, null, 0, 0))
            FeatureCardShell(LiloFeature.NOTES, {}, {}, NoteReportDetail("", "", 0, null, 0))
            FeatureCardShell(LiloFeature.PASSWORDS, {}, {}, PasswordReportDetail(0, "", "", 0))
        }
    }
}

@AppPreviews
@Composable
private fun CompletedHomeTaskPreview() {
    LiloPreviewWrapper {
        FeatureCardShell(LiloFeature.TASKS, {}, {},
            TaskReportDetail("Buy a birthday gift", null, 0, 3, 42, 0, nextTaskDone = true, nextTaskDescription = "A book and a handwritten card"))
    }
}
