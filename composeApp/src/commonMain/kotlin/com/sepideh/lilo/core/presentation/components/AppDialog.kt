package com.sepideh.lilo.core.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun AppDialog(
    dialogModel: DialogModel,
) {
    with(dialogModel) {
        Dialog(onDismissRequest = onDismissRequest) {
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(modifier = Modifier.padding(24.dp)) {
                    content()
                }
            }
        }
    }

}

data class DialogModel(
    val content: @Composable () -> Unit,
    val onDismissRequest: () -> Unit
)