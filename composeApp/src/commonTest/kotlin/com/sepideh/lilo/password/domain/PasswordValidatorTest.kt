package com.sepideh.lilo.password.domain

import com.sepideh.lilo.password.domain.model.Password
import com.sepideh.lilo.password.domain.validation.PasswordValidationError
import com.sepideh.lilo.password.domain.validation.PasswordValidator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PasswordValidatorTest {
    private val valid = Password(title = "Mail", serviceId = "google", secret = "secret")

    @Test fun reportsAllIndependentErrors() {
        assertEquals(
            setOf(PasswordValidationError.IdentifierRequired, PasswordValidationError.ServiceRequired,
                PasswordValidationError.SecretRequired, PasswordValidationError.InvalidWebsite),
            PasswordValidator.validate(Password(website = "invalid")),
        )
    }

    @Test fun eitherIdentifierIsEnoughAndUsernameHasNoFormatRequirement() {
        assertTrue(PasswordValidator.validate(valid.copy(title = " ", username = "anything")).isEmpty())
        assertTrue(PasswordValidator.validate(valid.copy(username = " ")).isEmpty())
        assertEquals(setOf(PasswordValidationError.IdentifierRequired),
            PasswordValidator.validate(valid.copy(title = " \t", username = "\n")))
    }

    @Test fun serviceMustBeNonblankButExistenceBelongsToPersistence() {
        for (service in listOf(null, "", " \t\n")) {
            assertEquals(setOf(PasswordValidationError.ServiceRequired),
                PasswordValidator.validate(valid.copy(serviceId = service)))
        }
        assertTrue(PasswordValidator.validate(valid.copy(serviceId = "unknown")).isEmpty())
    }

    @Test fun rejectsEmptyAndWhitespaceOnlySecrets() {
        for (secret in listOf("", " ", "\t\r\n", "\u00a0\u2003")) {
            assertEquals(setOf(PasswordValidationError.SecretRequired),
                PasswordValidator.validate(valid.copy(secret = secret)))
        }
        assertEquals(setOf(PasswordValidationError.SecretRequired, PasswordValidationError.SecretTooLong),
            PasswordValidator.validate(valid.copy(secret = " ".repeat(257))))
    }

    @Test fun preservesRawValuesAndAcceptsExactBoundaries() {
        val secret = " " + "ر".repeat(254) + " "
        val title = "  " + "ع".repeat(60) + "  "
        val password = valid.copy(title = title, secret = secret, username = " user ", website = " example.com ")
        assertTrue(PasswordValidator.validate(password).isEmpty())
        assertEquals(secret, password.secret)
        assertEquals(title, password.title)
        assertEquals(" user ", password.username)
        assertEquals(" example.com ", password.website)
    }

    @Test fun lengthRulesUseStringLengthAndIncludeSecretWhitespace() {
        assertEquals(setOf(PasswordValidationError.TitleTooLong),
            PasswordValidator.validate(valid.copy(title = " " + "a".repeat(61) + " ")))
        assertEquals(setOf(PasswordValidationError.SecretTooLong),
            PasswordValidator.validate(valid.copy(secret = " " + "x".repeat(256))))
        assertTrue(PasswordValidator.validate(valid.copy(secret = "🔑".repeat(128))).isEmpty())
        assertEquals(setOf(PasswordValidationError.SecretTooLong),
            PasswordValidator.validate(valid.copy(secret = "🔑".repeat(129))))
    }

    @Test fun delegatesOptionalWebsiteRules() {
        for (website in listOf("", " \t", "example.com", "HTTP://example.com:8080/a?b=c#d", "مثال.ir/راهنما")) {
            assertTrue(PasswordValidator.validate(valid.copy(website = website)).isEmpty(), website)
        }
        for (website in listOf("a", "ftp://example.com", "a b.com", "user@example.com", "a.com:65536", "a.com/%zz")) {
            assertEquals(setOf(PasswordValidationError.InvalidWebsite),
                PasswordValidator.validate(valid.copy(website = website)), website)
        }
    }
}
