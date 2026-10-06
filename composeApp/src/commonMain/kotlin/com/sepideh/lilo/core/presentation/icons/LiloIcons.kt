package com.sepideh.lilo.core.presentation.icons

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.vectorResource

/** Project-owned line icons: one grid, rounded 1.6px strokes, directional mirroring in the asset. */
object LiloIcons {
    val Back: ImageVector @Composable get() = vectorResource(Res.drawable.lilo_back)
    val Search: ImageVector @Composable get() = vectorResource(Res.drawable.lilo_search)
    val Filter: ImageVector @Composable get() = vectorResource(Res.drawable.lilo_filter)
    val Close: ImageVector @Composable get() = vectorResource(Res.drawable.lilo_close)
    val Add: ImageVector @Composable get() = vectorResource(Res.drawable.lilo_add)
    val Check: ImageVector @Composable get() = vectorResource(Res.drawable.lilo_check)
    val Edit: ImageVector @Composable get() = vectorResource(Res.drawable.lilo_edit)
    val Description: ImageVector @Composable get() = vectorResource(Res.drawable.lilo_description)
    val Groups: ImageVector @Composable get() = vectorResource(Res.drawable.lilo_groups)
    val Bell: ImageVector @Composable get() = vectorResource(Res.drawable.lilo_bell)
    val Reminder: ImageVector @Composable get() = vectorResource(Res.drawable.lilo_reminder)
    val Images: ImageVector @Composable get() = vectorResource(Res.drawable.lilo_images)
    val Delete: ImageVector @Composable get() = vectorResource(Res.drawable.lilo_delete)
    val Chevron: ImageVector @Composable get() = vectorResource(Res.drawable.lilo_chevron)
    val Empty: ImageVector @Composable get() = vectorResource(Res.drawable.lilo_empty)
}
