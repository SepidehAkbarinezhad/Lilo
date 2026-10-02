package com.sepideh.lilo.task.presentation.reminder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.sepideh.lilo.task.domain.reminder.ReminderPermissions
import com.sepideh.lilo.task.domain.usecase.TaskMutations
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.awaitCancellation
import org.koin.compose.koinInject

/** Restores Android alarms and replenishes iOS future-start occurrences on foreground entry. */
@Composable
fun ReminderLifecycleObserver() {
    val owner = LocalLifecycleOwner.current
    val mutations = koinInject<TaskMutations>()
    val permissions = koinInject<ReminderPermissions>()
    LaunchedEffect(owner, mutations, permissions) {
        owner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            try {
                if (permissions.hasAccess()) mutations.restoreReminders()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                println("Reminder restoration failed: ${e.message}")
            }
            awaitCancellation()
        }
    }
}
