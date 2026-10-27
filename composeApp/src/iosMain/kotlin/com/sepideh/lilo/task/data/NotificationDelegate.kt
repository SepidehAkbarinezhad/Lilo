package com.sepideh.lilo.task.data


import com.sepideh.lilo.core.domain.model.AppLanguage
import platform.UserNotifications.*
import platform.darwin.NSObject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.distinctUntilChanged
import com.sepideh.lilo.task.domain.usecase.TaskMutations
import com.sepideh.lilo.task.presentation.reminder.*
import platform.Foundation.NSLog
import org.koin.mp.KoinPlatform.getKoin
import com.sepideh.lilo.settings.domain.repo.UserPreferencesRepository

class NotificationDelegate : NSObject(), UNUserNotificationCenterDelegateProtocol {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var registration: Job? = null

    fun registerActions() {
        if (registration != null) return
        registration = scope.launch {
            try {
                getKoin().get<UserPreferencesRepository>().userPreferences.map { it.language }.distinctUntilChanged().collect { language ->
                    val category = taskNotificationCategory(language)
                    UNUserNotificationCenter.currentNotificationCenter().setNotificationCategories(setOf(category))
                }
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (failure: Exception) { NSLog("Lilo: could not register task notification action: %@", failure.message.orEmpty()) }
        }
    }

    override fun userNotificationCenter(
        center: UNUserNotificationCenter,
        didReceiveNotificationResponse: UNNotificationResponse,
        withCompletionHandler: () -> Unit
    ) {
        if (didReceiveNotificationResponse.actionIdentifier != COMPLETE_TASK_ACTION) {
            withCompletionHandler()
            return
        }
        val content = didReceiveNotificationResponse.notification.request.content
        val taskId = (content.userInfo["taskId"] as? String)?.toLongOrNull()
        if (content.categoryIdentifier != TASK_NOTIFICATION_CATEGORY || taskId == null || taskId < 0) {
            withCompletionHandler()
            return
        }
        scope.launch {
            try { getKoin().get<TaskMutations>().setCompleted(taskId, true) }
            catch (cancelled: CancellationException) { throw cancelled }
            catch (failure: Exception) { NSLog("Lilo: could not complete task from notification: %@", failure.message.orEmpty()) }
            finally { withCompletionHandler() }
        }
    }

    override fun userNotificationCenter(
        center: UNUserNotificationCenter,
        willPresentNotification: UNNotification,
        withCompletionHandler: (UNNotificationPresentationOptions) -> Unit
    ) {
        scope.launch {
            val enabled = try {
                getKoin().get<UserPreferencesRepository>().userPreferences.first().notificationsEnabled
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { false }
            withCompletionHandler(if (enabled) UNNotificationPresentationOptionAlert or UNNotificationPresentationOptionSound else 0uL)
        }
    }
}

internal suspend fun taskNotificationCategory(language: AppLanguage): UNNotificationCategory {
    // Finish resource loading before bridging arguments to Objective-C: suspension here
    // can outlive the autoreleased NSString created for an earlier argument.
    val title = markDoneNotificationLabel(language)
    val action = UNNotificationAction.actionWithIdentifier(COMPLETE_TASK_ACTION, title, 0uL)
    return UNNotificationCategory.categoryWithIdentifier(TASK_NOTIFICATION_CATEGORY, listOf(action), emptyList<String>(), 0uL)
}
