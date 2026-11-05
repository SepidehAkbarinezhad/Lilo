package com.sepideh.lilo.fcm

import java.net.URI
import java.net.URL

internal data class FcmNotificationContent(val title: String, val body: String, val imageUrl: URL?) {
    companion object {
        fun resolve(
            data: Map<String, String>, notificationTitle: String?, notificationBody: String?,
            notificationImage: String?, defaultTitle: String, defaultBody: String,
        ) = FcmNotificationContent(
            data["title"].nonBlank() ?: notificationTitle.nonBlank() ?: defaultTitle,
            data["body"].nonBlank() ?: notificationBody.nonBlank() ?: defaultBody,
            httpsImageUrl(data["image_url"].nonBlank() ?: notificationImage.nonBlank()),
        )

        private fun String?.nonBlank() = this?.takeIf { it.isNotBlank() }

        internal fun httpsImageUrl(value: String?): URL? = try {
            value?.let { URI(it) }?.takeIf {
                it.scheme.equals("https", ignoreCase = true) && !it.host.isNullOrBlank() &&
                    it.userInfo == null && (it.port == -1 || it.port in 1..65535)
            }?.toURL()
        } catch (_: IllegalArgumentException) {
            null
        } catch (_: java.net.URISyntaxException) {
            null
        } catch (_: java.net.MalformedURLException) {
            null
        }
    }
}
