package com.sepideh.lilo.fcm

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FcmNotificationContentTest {
    @Test
    fun dataOverridesEachNotificationField() {
        val content = resolve(mapOf("title" to "Data title", "body" to "Data body",
            "image_url" to "https://example.com/data.png"))
        assertEquals("Data title", content.title)
        assertEquals("Data body", content.body)
        assertEquals("https://example.com/data.png", content.imageUrl.toString())
    }

    @Test
    fun partialAndBlankDataFallsBackPerField() {
        val content = resolve(mapOf("title" to "Data title", "body" to " ", "image_url" to ""))
        assertEquals("Data title", content.title)
        assertEquals("Notification body", content.body)
        assertEquals("https://example.com/notification.png", content.imageUrl.toString())
    }

    @Test
    fun dataOnlyAndEmptyMessagesHaveTextDefaults() {
        val content = FcmNotificationContent.resolve(mapOf("body" to "Data body"),
            null, null, null, "Lilo", "Default body")
        assertEquals("Lilo", content.title)
        assertEquals("Data body", content.body)
        assertNull(content.imageUrl)
        assertEquals("Default body", FcmNotificationContent.resolve(emptyMap(),
            null, null, null, "Lilo", "Default body").body)
    }

    @Test
    fun unsafeOrMalformedImagesAreTextOnly() {
        listOf("http://example.com/a.png", "file:///a.png", "https:///a.png",
            "https://user:password@example.com/a.png", "https://example.com:99999/a.png",
            "not a URL").forEach { value ->
            assertNull(resolve(mapOf("image_url" to value)).imageUrl, value)
        }
    }

    private fun resolve(data: Map<String, String>) = FcmNotificationContent.resolve(
        data, "Notification title", "Notification body", "https://example.com/notification.png",
        "Lilo", "Default body",
    )
}
