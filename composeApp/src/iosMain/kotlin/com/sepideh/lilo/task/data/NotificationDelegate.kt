package com.sepideh.lilo.task.data


import platform.UserNotifications.*
import platform.darwin.NSObject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.koin.mp.KoinPlatform.getKoin
import com.sepideh.lilo.settings.domain.repo.UserPreferencesRepository

class NotificationDelegate : NSObject(), UNUserNotificationCenterDelegateProtocol {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

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