package com.sepideh.lilo.password.domain

import com.sepideh.lilo.password.domain.model.Password
import com.sepideh.lilo.password.domain.model.matching

import kotlin.test.*

class PasswordRulesTest {
    @Test fun identifierRequiresAtLeastOneNonBlankField() {
        assertFalse(Password(title = " ", username = " ").hasIdentifier)
        assertTrue(Password(title = "Personal").hasIdentifier)
        assertTrue(Password(username = "+989123456789").hasIdentifier)
    }
    @Test fun searchExcludesSecretsAndPrivateNotes() {
        val records = listOf(Password(title = "Bank", username = "user", secret = "hidden-secret", notes = "hidden-note"))
        assertTrue(records.matching("hidden", false).isEmpty())
        assertEquals(records, records.matching(" USER ", false))
    }
    @Test fun searchNormalizesPersianAndSortUsesStableIds() {
        val records = listOf(Password(id = "b", title = "کاری", updatedAt = 1), Password(id = "a", title = "Alpha", updatedAt = 2))
        assertEquals("b", records.matching("كاري", false).single().id)
        assertEquals("a", records.matching("", false).first().id)
        assertEquals("a", records.matching("", true).first().id)
    }
    @Test fun debugStringRedactsEveryField() {
        val record = Password(title = "private-name", secret = "private-secret", username = "private-user")
        assertEquals("Password(redacted)", record.toString())
    }
}
