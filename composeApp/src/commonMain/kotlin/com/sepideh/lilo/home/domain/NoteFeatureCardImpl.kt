package com.sepideh.lilo.home.domain

import androidx.compose.runtime.Composable
import com.sepideh.lilo.home.presentation.model.LiloFeature
import com.sepideh.lilo.home.presentation.model.NoteReportDetail
import com.sepideh.lilo.home.presentation.model.TaskReportDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class NoteFeatureCardImpl : FeatureCard<NoteReportDetail> {
    override val feature = LiloFeature.NOTES

    override fun getReportDetailStrategy(): ReportDetailStrategy<NoteReportDetail> =
        object : ReportDetailStrategy<NoteReportDetail> {
            override fun observeReportDetail(): Flow<NoteReportDetail> {
              return  flowOf  (NoteReportDetail(
                  latestTitle = "",
                  latestSnippet = "",
                  totalCount = 12,
                  thumbnailUrl = "",
                  subTitleReportCount = 2
              ) )
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