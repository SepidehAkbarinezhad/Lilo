package com.sepideh.lilo.home.domain

import androidx.compose.runtime.Composable
import com.sepideh.lilo.home.presentation.model.LiloFeature
import com.sepideh.lilo.home.presentation.model.ReportDetail
import kotlinx.coroutines.flow.Flow


interface FeatureCard<T : ReportDetail> {
    val feature: LiloFeature
    fun getReportDetailStrategy(): ReportDetailStrategy<T>
    fun getReportRender(): ReportRenderStrategy<T>
}


interface ReportDetailStrategy<O : ReportDetail> {
    fun observeReportDetail(): Flow<O>

}

interface ReportRenderStrategy<I : ReportDetail> {
    @Composable
    fun Render(detail: I)
}