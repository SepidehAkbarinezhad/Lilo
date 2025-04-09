package com.sepideh.lilo.core.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.sepideh.lilo.R

@Composable
actual fun AppImageFromResource(modifier: Modifier) {
    Image(
        painter = painterResource(R.drawable.empty_box),
        contentDescription = "Local image from resources"
    )
}