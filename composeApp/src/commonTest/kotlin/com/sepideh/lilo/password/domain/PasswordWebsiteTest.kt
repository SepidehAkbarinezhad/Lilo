package com.sepideh.lilo.password.domain

import com.sepideh.lilo.password.domain.model.Password
import com.sepideh.lilo.password.domain.model.matching
import com.sepideh.lilo.password.domain.model.normalizePasswordWebsite

import kotlin.test.*

class PasswordWebsiteTest {
    @Test fun optionalWebsiteAndInternationalNames() {
        assertEquals("", normalizePasswordWebsite("  "))
        assertEquals("https://example.com", normalizePasswordWebsite(" example.com "))
        assertEquals("http://example.com:8080/a?b=c#d", normalizePasswordWebsite("HTTP://example.com:8080/a?b=c#d"))
        assertEquals("https://مثال.ir/راهنما", normalizePasswordWebsite("مثال.ir/راهنما"))
        assertEquals("https://xn--mgbh0fb.ir", normalizePasswordWebsite("xn--mgbh0fb.ir"))
    }
    @Test fun invalidWebsitesRejected() {
        listOf("a", "https://", "ftp://example.com", "a b.com", "-a.com", "a..com", "a_.com", "a.com:abc", "a.com:65536", "user@example.com", "256.0.0.1", "a.com/%zz").forEach {
            assertNull(normalizePasswordWebsite(it), it)
        }
    }
    @Test fun searchAndSortUseServiceWhenTitleIsMissing() {
        val google = Password(id = "1", serviceId = "google", serviceName = "Google")
        val apple = Password(id = "2", serviceId = "apple", serviceName = "Apple", title = "Work")
        assertEquals(listOf(apple), listOf(google, apple).matching("apple", false))
        assertEquals(listOf(google, apple), listOf(apple, google).matching("", true))
    }
}
