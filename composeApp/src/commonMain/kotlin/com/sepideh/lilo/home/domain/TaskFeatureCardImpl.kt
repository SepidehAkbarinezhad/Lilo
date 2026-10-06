package com.sepideh.lilo.home.domain

import androidx.compose.runtime.Composable
import com.sepideh.lilo.home.presentation.model.LiloFeature
import com.sepideh.lilo.home.presentation.model.TaskReportDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskFeatureCardImpl(private val repository: com.sepideh.lilo.task.domain.repository.TaskRepository) : FeatureCard<TaskReportDetail> {
    override val feature = LiloFeature.TASKS

    override fun getReportDetailStrategy(): ReportDetailStrategy<TaskReportDetail> =
        object : ReportDetailStrategy<TaskReportDetail> {
            override fun observeReportDetail(): Flow<TaskReportDetail> {
                return repository.getAllTasks().map { tasks ->
                    taskHomeReport(tasks)
                }
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