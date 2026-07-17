package com.sepideh.lilo.home.domain

import androidx.compose.runtime.Composable
import com.sepideh.lilo.home.presentation.model.LiloFeature
import com.sepideh.lilo.home.presentation.model.ReportDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow


class FeatureCardFactory(
    private val cards: List<FeatureCard<*>>
) {
    private val byFeature = cards.associateBy { it.feature }

    @Suppress("UNCHECKED_CAST")
    fun cardFor(feature: LiloFeature): FeatureCard<ReportDetail> =
        byFeature.getValue(feature) as FeatureCard<ReportDetail>
}


 fun fakeFeatureCardFactory(): FeatureCardFactory =
    FeatureCardFactory(
        cards = LiloFeature.entries.map { feature ->
            object : FeatureCard<ReportDetail> {
                override val feature: LiloFeature = feature

                override fun getReportDetailStrategy(): ReportDetailStrategy<ReportDetail> =
                    object : ReportDetailStrategy<ReportDetail> {
                        override fun observeReportDetail(): Flow<ReportDetail> = emptyFlow()
                    }

                override fun getReportRender(): ReportRenderStrategy<ReportDetail> =
                    object : ReportRenderStrategy<ReportDetail> {
                        @Composable
                        override fun Render(detail: ReportDetail) {
                            // no-op for preview — state is empty anyway
                        }
                    }
            }
        }
    )