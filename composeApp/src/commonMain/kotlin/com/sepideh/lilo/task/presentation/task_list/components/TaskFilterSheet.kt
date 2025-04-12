package com.sepideh.lilo.task.presentation.task_list.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.data.ScreenSize
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppBottomSheet
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.task.presentation.task_list.TaskListState
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.filter_label
import org.jetbrains.compose.resources.stringResource

@Composable
fun TaskFilterSheet(
    state: TaskListState,
    modifier: Modifier = Modifier,
) {

    val height = (.5 * ScreenSize.heightDp).dp
    AppBottomSheet(
        visible = state.isFilterSheetOpen,
        height = height,
        modifier = modifier.fillMaxWidth()
    ) {

        AppText(text = stringResource(Res.string.filter_label), textType = TextType.SubTitle)

    }
}