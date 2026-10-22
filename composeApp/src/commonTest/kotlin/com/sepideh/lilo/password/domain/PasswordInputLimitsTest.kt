package com.sepideh.lilo.password.domain

import com.sepideh.lilo.password.domain.model.Password
import com.sepideh.lilo.password.domain.model.requirePasswordFieldLengths
import com.sepideh.lilo.password.domain.model.secretTooLong

import kotlin.test.*

class PasswordInputLimitsTest {
    @Test fun acceptsBoundariesAndTrimsOnlyTitleForValidation() {
        val secret = " " + "ر".repeat(254) + " "
        val password = Password(title = "  " + "ع".repeat(60) + "  ", secret = secret)
        requirePasswordFieldLengths(password)
        assertEquals(secret, password.secret)
    }
    @Test fun rejectsEachOversizedFieldIndependently() {
        assertFailsWith<IllegalArgumentException> { requirePasswordFieldLengths(Password(title = "a".repeat(61), secret = "valid")) }
        assertFailsWith<IllegalArgumentException> { requirePasswordFieldLengths(Password(title = "valid", secret = "x".repeat(257))) }
    }
    @Test fun passwordWhitespaceCountsAndNothingIsTruncated() {
        val password = Password(title = "Valid", secret = " " + "x".repeat(256))
        assertTrue(password.secretTooLong)
        assertEquals(257, password.secret.length)
        assertFailsWith<IllegalArgumentException> { requirePasswordFieldLengths(password) }
    }
}
