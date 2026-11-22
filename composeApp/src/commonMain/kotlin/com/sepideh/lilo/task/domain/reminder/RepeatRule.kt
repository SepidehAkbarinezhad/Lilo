package com.sepideh.lilo.task.domain.reminder

enum class RepeatRule(val intervalDays: Int) {
    NONE(0), DAILY(1), WEEKLY(7), MONTHLY(0);

    companion object {
        fun fromCode(code: String): RepeatRule = entries.firstOrNull { it.name == code } ?: NONE
    }
}
