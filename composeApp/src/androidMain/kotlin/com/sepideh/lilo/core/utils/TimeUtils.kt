package com.sepideh.lilo.core.utils

import ir.huri.jcal.JalaliCalendar
import java.util.Date
import java.util.GregorianCalendar

fun jalaliToEpochMillis(jalaliDate: JalaliCalendar): Long {
    val jalali = JalaliCalendar(jalaliDate.year, jalaliDate.month, jalaliDate.day)
    val gregorian = jalali.toGregorian().timeInMillis
    return gregorian
}

fun millisToJalali(millis: Long): JalaliCalendar {
    val date = Date(millis)
    val gregorian = GregorianCalendar()
    gregorian.time = date
    return JalaliCalendar(gregorian)
}