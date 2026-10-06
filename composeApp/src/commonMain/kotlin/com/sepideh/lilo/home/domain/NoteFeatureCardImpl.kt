package com.sepideh.lilo.home.domain

import androidx.compose.runtime.Composable
import com.sepideh.lilo.home.presentation.model.LiloFeature
import com.sepideh.lilo.home.presentation.model.NoteReportDetail
import com.sepideh.lilo.home.presentation.model.TaskReportDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteFeatureCardImpl(private val repository: com.sepideh.lilo.note.domain.repository.NoteRepository) : FeatureCard<NoteReportDetail> {
    override val feature = LiloFeature.NOTES

    override fun getReportDetailStrategy(): ReportDetailStrategy<NoteReportDetail> =
        object : ReportDetailStrategy<NoteReportDetail> {
            override fun observeReportDetail(): Flow<NoteReportDetail> {
                return repository.getAllNotes().map { notes ->
                    noteHomeReport(notes)
                }
            }

        }

    override fun getReportRender(): ReportRenderStrategy<NoteReportDetail> =
        object : ReportRenderStrategy<NoteReportDetail> {
            @Composable
            override fun Render(detail: NoteReportDetail) {
                // task-specific row UI
            }
        }
}