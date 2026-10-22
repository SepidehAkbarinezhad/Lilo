package com.sepideh.lilo.password.domain

import com.sepideh.lilo.password.domain.model.Password
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class PasswordSerializationTest {
    @OptIn(ExperimentalSerializationApi::class)
    @Test fun packageMovePreservesStoredPayloadAndSerializerName() {
        val stored = """{"id":"one","title":"Mail","username":"user","secret":" private ","website":"https://example.com","notes":"sensitive","createdAt":1,"updatedAt":2,"serviceId":"google","serviceName":"Google","serviceNameFa":"گوگل"}"""
        val expected = Password(id = "one", title = "Mail", username = "user", secret = " private ",
            website = "https://example.com", notes = "sensitive", createdAt = 1, updatedAt = 2,
            serviceId = "google", serviceName = "Google", serviceNameFa = "گوگل")
        assertEquals(expected, Json.decodeFromString<Password>(stored))
        assertEquals(stored, Json.encodeToString(expected))
        assertEquals("com.sepideh.lilo.password.domain.Password", Password.serializer().descriptor.serialName)
    }
}
