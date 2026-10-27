package com.sepideh.lilo.task.data

import com.sepideh.lilo.core.domain.model.AppLanguage
import com.sepideh.lilo.task.presentation.reminder.COMPLETE_TASK_ACTION
import com.sepideh.lilo.task.presentation.reminder.TASK_NOTIFICATION_CATEGORY
import kotlinx.coroutines.test.runTest
import platform.UserNotifications.UNNotificationAction
import kotlin.test.Test
import kotlin.test.assertEquals

class NotificationCategoryTest {
    @Test fun localizedActionsKeepTheirTaskCompletionContract() = runTest {
        for ((language, title) in listOf(AppLanguage.EN to "Mark done", AppLanguage.FA to "انجام شد", AppLanguage.EN to "Mark done")) {
            val category = taskNotificationCategory(language)
            assertEquals(TASK_NOTIFICATION_CATEGORY, category.identifier)
            val action = category.actions.single() as UNNotificationAction
            assertEquals(COMPLETE_TASK_ACTION, action.identifier)
            assertEquals(title, action.title)
            assertEquals(0uL, action.options)
        }
    }
}
