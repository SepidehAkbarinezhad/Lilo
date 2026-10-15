package com.sepideh.lilo.settings.domain.usecase

import com.sepideh.lilo.settings.domain.repo.UserPreferencesRepository
import com.sepideh.lilo.task.domain.usecase.TaskMutations

class UpdateReminderNotifications(
    private val preferences: UserPreferencesRepository,
    private val tasks: TaskMutations,
) {
    suspend operator fun invoke(enabled: Boolean) {
        tasks.updateNotificationPreference { preferences.updateNotifications(enabled) }
    }
}
