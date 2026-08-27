package com.sepideh.lilo.home.presentation.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.sepideh.lilo.ui.icons.TaskCardIcon
import com.sepideh.lilo.ui.theme.LiloColors
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.task_feature_subTitle
import lilo.composeapp.generated.resources.task_feature_title
import org.jetbrains.compose.resources.StringResource

enum class LiloFeature(
    val titleRes: StringResource,
    val subTitleRes: StringResource ,
    val iconRes: ImageVector,
    val accentColor: (LiloColors) -> Color// Lambda that takes the light/dark palette in runtime and returns the active Color
) {
    TASKS(
        titleRes = Res.string.task_feature_title,
        subTitleRes = Res.string.task_feature_subTitle,
        iconRes = TaskCardIcon,
        accentColor = { colors -> colors.taskColor }),
    /*NOTES(
        titleRes = Res.string.note_feature_title,
        iconRes = NoteCardIcon,
        accentColor = { colors -> colors.noteColor }),
    EXPENSES(
        titleRes = Res.string.expence_feature_title,
        iconRes = ExpenseCardIcon,
        accentColor = { colors -> colors.expenseColor }),
    PASSWORDS(
        titleRes = Res.string.password_feature_title,
        iconRes = PasswordCardIcon,
        accentColor = { colors -> colors.passwordColor }
    )*/
}