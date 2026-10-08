package com.sepideh.lilo.home.domain

import androidx.compose.runtime.Composable
import com.sepideh.lilo.home.presentation.model.LiloFeature
import com.sepideh.lilo.home.presentation.model.TaskReportDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.delay

class TaskFeatureCardImpl(private val repository: com.sepideh.lilo.task.domain.repository.TaskRepository) : FeatureCard<TaskReportDetail> {
    override val feature = LiloFeature.TASKS

    override fun getReportDetailStrategy(): ReportDetailStrategy<TaskReportDetail> =
        object : ReportDetailStrategy<TaskReportDetail> {
            override fun observeReportDetail(): Flow<TaskReportDetail> {
                // Refresh while Home is observed so midnight and passed reminder times update the preview.
                val clock = flow {
                    while (true) {
                        emit(kotlin.time.Clock.System.now().toEpochMilliseconds())
                        delay(60_000)
                    }
                }
                return combine(repository.getAllTasks(), clock) { tasks, now -> taskHomeReport(tasks, now) }
            }

        }

    override fun getReportRender(): ReportRenderStrategy<TaskReportDetail> =
        object : ReportRenderStrategy<TaskReportDetail> {
            @Composable
            override fun Render(detail: TaskReportDetail) {
                // task-specific row UI
            }
        }
}