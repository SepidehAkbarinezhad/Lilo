package com.sepideh.lilo.task.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gmail.hamedvakhide.compose_jalali_datepicker.JalaliDatePickerDialog
import ir.huri.jcal.JalaliCalendar


@Composable
fun JalaliDatePicker(modifier: Modifier = Modifier) {
    val openDialog = remember { mutableStateOf(true) }

    JalaliDatePickerDialog(
        openDialog = openDialog,
        initialDate = JalaliCalendar(),
        onSelectDay = {},
        onConfirm = {}
    )
}

@Preview
@Composable
private fun JalaliDatePickerPrev() {
 JalaliDatePicker()
}


