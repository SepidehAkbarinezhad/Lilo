package com.sepideh.lilo.core.presentation.components.form

import com.sepideh.lilo.ui.theme.FeatureColors
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppPreviews
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.core.presentation.components.LiloPreviewWrapper
import com.sepideh.lilo.core.presentation.icons.LiloIcons
import com.sepideh.lilo.ui.theme.LiloExtendedTheme
import com.sepideh.lilo.ui.theme.LiloSize
import com.sepideh.lilo.ui.theme.LiloSpacing
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.save_task_action
import lilo.composeapp.generated.resources.saving_action
import org.jetbrains.compose.resources.StringResource

@Composable
fun FormSaveButton(
    featureColors: FeatureColors,
    label: StringResource,
    enabled: Boolean,
    isSaving: Boolean,
    onSave: () -> Unit,
) {
    Box(
        Modifier.fillMaxWidth().navigationBarsPadding()
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
            .padding(horizontal = LiloSpacing.Screen, vertical = LiloSpacing.Item),
    ) {
        Button(
            onClick = onSave,
            enabled = enabled && !isSaving,
            modifier = Modifier.fillMaxWidth().height(56.dp)
                .semantics { liveRegion = LiveRegionMode.Polite },
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = featureColors.primary,
                contentColor = featureColors.onPrimary,
                disabledContainerColor = featureColors.primary,
                disabledContentColor = featureColors.onPrimary.copy(alpha = if (isSaving) 1f else .38f),
            ),
        ) {
            if (isSaving) {
                CircularProgressIndicator(Modifier.size(LiloSize.SmallIcon), color = featureColors.onPrimary, strokeWidth = 2.dp)
            } else {
                Icon(LiloIcons.Check, contentDescription = null, modifier = Modifier.size(LiloSize.SmallIcon))
            }
            Spacer(Modifier.width(LiloSpacing.Small))
            AppText(text = if (isSaving) Res.string.saving_action else label, textType = TextType.Action, maxLines = 1)
        }
    }
}

@AppPreviews
@Composable
private fun FormSaveButtonPreview() {
    LiloPreviewWrapper {
        Column {
            listOf(true to false, false to false, false to true).forEach { (enabled, saving) ->
                FormSaveButton(LiloExtendedTheme.colors.tasks, Res.string.save_task_action, enabled, saving, {})
            }
        }
    }
}
