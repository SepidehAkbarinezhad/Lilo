package com.sepideh.lilo.home.domain
import androidx.compose.runtime.Composable
import com.sepideh.lilo.home.presentation.model.*
import kotlinx.coroutines.flow.map
import com.sepideh.lilo.password.domain.PasswordRepository
/** Home observes public entry metadata only; it never decrypts credentials. */
class PasswordFeatureCardImpl(private val repository: PasswordRepository) : FeatureCard<PasswordReportDetail> {
    override val feature = LiloFeature.PASSWORDS
    override fun getReportDetailStrategy() = object : ReportDetailStrategy<PasswordReportDetail> {
        override fun observeReportDetail() = repository.observePasswords().map(::passwordHomeReport)
    }
    override fun getReportRender() = object : ReportRenderStrategy<PasswordReportDetail> {
        @Composable override fun Render(detail: PasswordReportDetail) {}
    }
}
