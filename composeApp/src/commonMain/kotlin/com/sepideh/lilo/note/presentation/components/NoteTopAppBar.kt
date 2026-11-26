package com.sepideh.lilo.note.presentation.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.core.presentation.icons.LiloIcons
import com.sepideh.lilo.ui.theme.LiloExtendedTheme
import com.sepideh.lilo.ui.theme.LiloSize
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.back_action
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteTopAppBar(
    title: String,
    onBack: () -> Unit,
    selectableTitle: Boolean = false,
    titleTextType: TextType = TextType.SectionTitle,
) {
    val titleContent: @Composable () -> Unit = {
        AppText(text = title, textType = titleTextType, maxLines = 2)
    }
    TopAppBar(
        title = {
            if (selectableTitle) SelectionContainer { titleContent() } else titleContent()
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(LiloIcons.Back, stringResource(Res.string.back_action), Modifier.size(LiloSize.Icon))
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.background,
            navigationIconContentColor = LiloExtendedTheme.colors.title,
            titleContentColor = LiloExtendedTheme.colors.title,
        ),
        windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
    )
}
