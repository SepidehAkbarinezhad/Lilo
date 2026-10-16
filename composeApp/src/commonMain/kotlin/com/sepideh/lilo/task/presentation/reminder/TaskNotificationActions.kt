package com.sepideh.lilo.task.presentation.reminder

import com.sepideh.lilo.core.domain.model.AppLanguage
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.notification_mark_done_en
import lilo.composeapp.generated.resources.notification_mark_done_fa
import org.jetbrains.compose.resources.getString

internal const val TASK_NOTIFICATION_CATEGORY = "task_reminders"
internal const val COMPLETE_TASK_ACTION = "com.sepideh.lilo.COMPLETE_TASK"

// Background notifications must use the saved app language, independently of the system locale.
internal suspend fun markDoneNotificationLabel(language: AppLanguage): String = getString(
    if (language == AppLanguage.FA) Res.string.notification_mark_done_fa else Res.string.notification_mark_done_en
)
