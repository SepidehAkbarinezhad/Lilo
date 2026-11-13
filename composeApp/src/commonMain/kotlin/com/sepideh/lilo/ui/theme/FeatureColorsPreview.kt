package com.sepideh.lilo.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.components.*
import com.sepideh.lilo.core.presentation.components.form.FormSaveButton
import com.sepideh.lilo.core.presentation.components.header.FeatureListHeader
import com.sepideh.lilo.core.presentation.icons.LiloIcons
import com.sepideh.lilo.home.presentation.components.FeatureCardShell
import com.sepideh.lilo.home.presentation.model.*
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

/** Review the same semantic roles together in both themes and reading directions. */
@Composable
private fun FeatureColorRoles(feature: LiloFeature) {
    LiloPreviewWrapper {
        val colors = feature.colors(LiloExtendedTheme.colors)
        val report = when (feature) {
            LiloFeature.TASKS -> TaskReportDetail("Read a book", "15:00", 5, 12)
            LiloFeature.NOTES -> NoteReportDetail("Weekend trip", "Pack a book and a camera", 8, null, 8)
            LiloFeature.PASSWORDS -> PasswordReportDetail(6, "GitHub", "sepideh", 6, "github")
        }
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                FeatureListHeader(feature.titleRes, colors, false, "", Res.string.search_tasks_action, true,
                    { true }, {}, {}, {})
                Box(Modifier.padding(horizontal = 16.dp)) {
                    FeatureCardShell(feature, {}, {}, report)
                }
                Row(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    LiloSelectionChip(stringResource(Res.string.group_all), true, colors.accent, {})
                    LiloSelectionChip(stringResource(Res.string.no_group_label), false, colors.accent, {})
                    FloatingActionButton({}, shape = CircleShape, containerColor = colors.primary, contentColor = colors.onPrimary) {
                        Icon(LiloIcons.Add, null)
                    }
                }
                FormSaveButton(colors, Res.string.confirm_action, true, false, {})
                FeatureListHeader(feature.titleRes, colors, true, "", Res.string.search_tasks_action, false,
                    { true }, {}, {}, {})
            }
        }
    }
}

@AppPreviews
@Composable
private fun TaskColorRolesPreview() = FeatureColorRoles(LiloFeature.TASKS)

@AppPreviews
@Composable
private fun NoteColorRolesPreview() = FeatureColorRoles(LiloFeature.NOTES)

@AppPreviews
@Composable
private fun PasswordColorRolesPreview() = FeatureColorRoles(LiloFeature.PASSWORDS)
