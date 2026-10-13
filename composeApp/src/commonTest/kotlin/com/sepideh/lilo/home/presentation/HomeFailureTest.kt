package com.sepideh.lilo.home.presentation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelStore
import com.sepideh.lilo.home.domain.*
import com.sepideh.lilo.home.presentation.model.*
import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.task.domain.repository.TaskRepository
import com.sepideh.lilo.task.domain.reminder.*
import com.sepideh.lilo.task.domain.usecase.TaskMutations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class HomeFailureTest {
    @Test fun failedReportCanRetryWithoutDuplicatingHealthySubscriptions() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val store = ViewModelStore()
        try {
            var attempts = 0
            val card = object : FeatureCard<ReportDetail> {
                override val feature = LiloFeature.NOTES
                override fun getReportDetailStrategy() = object : ReportDetailStrategy<ReportDetail> {
                    override fun observeReportDetail(): Flow<ReportDetail> = flow {
                        attempts++
                        if (attempts == 1) error("Read failed")
                        emit(NoteReportDetail("Recovered", "", 1, null, 0))
                        kotlinx.coroutines.awaitCancellation()
                    }
                }
                override fun getReportRender() = object : ReportRenderStrategy<ReportDetail> {
                    @Composable override fun Render(detail: ReportDetail) = Unit
                }
            }
            val model = HomeViewModel(FeatureCardFactory(listOf(card)), TaskMutations(UnusedTasks(), UnusedScheduler()))
            store.put("home", model)
            model.onAction(HomeAction.ObserveFeature(LiloFeature.NOTES))
            runCurrent()
            assertEquals(setOf(LiloFeature.NOTES), model.state.value.failedFeatures)
            model.onAction(HomeAction.RetryFeature(LiloFeature.NOTES))
            runCurrent()
            assertTrue(model.state.value.failedFeatures.isEmpty())
            assertEquals("Recovered", (model.state.value.reportDetails[LiloFeature.NOTES] as NoteReportDetail).latestTitle)
            model.onAction(HomeAction.ObserveFeature(LiloFeature.NOTES))
            model.onAction(HomeAction.RetryFeature(LiloFeature.NOTES))
            runCurrent()
            assertEquals(2, attempts)
        } finally { store.clear(); Dispatchers.resetMain() }
    }
    private class UnusedTasks : TaskRepository {
        override fun getAllTasks(): Flow<List<Task>> = error("Not used")
        override fun getTasksByFilter(done: Boolean?, priority: List<Int>): Flow<List<Task>> = error("Not used")
        override suspend fun clearGroup(id: Long): Unit = error("Not used")
        override suspend fun deleteTask(id: Long): Unit = error("Not used")
        override suspend fun upsertTask(task: Task): Long = error("Not used")
        override suspend fun getTaskById(id: Long): Task? = error("Not used")
    }
    private class UnusedScheduler : ReminderScheduler {
        override suspend fun scheduleReminder(reminder: Reminder): Unit = error("Not used")
        override suspend fun cancelReminder(taskId: Long): Unit = error("Not used")
    }
}
