package com.sepideh.lilo.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/** Shared geometry for controls, cards and supporting surfaces. */
object LiloSpacing {
    val Screen = 20.dp
    val Section = 24.dp
    val Item = 12.dp
    val Small = 8.dp
    val Tiny = 4.dp
}
object LiloSize {
    val Icon = 24.dp
    val SmallIcon = 20.dp
    val TouchTarget = 48.dp
    val SelectionRow = 64.dp
    val IconBadge = 44.dp
}
val LiloShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp),
)
