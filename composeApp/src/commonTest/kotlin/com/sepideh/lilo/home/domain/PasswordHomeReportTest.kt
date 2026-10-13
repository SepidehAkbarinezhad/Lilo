package com.sepideh.lilo.home.domain

import com.sepideh.lilo.password.domain.Password
import kotlin.test.*

class PasswordHomeReportTest {
    @Test fun latestAdditionWinsEvenWhenAnOlderEntryWasRecentlyEdited() {
        val report = passwordHomeReport(listOf(
            Password(id = "older", serviceName = "Google", username = "older@mail.com", createdAt = 1, updatedAt = 100),
            Password(id = "latest", serviceId = "github", serviceName = "GitHub", title = "Personal", username = "sepideh", createdAt = 2)))
        assertEquals("GitHub", report.lastAddedService)
        assertEquals("github", report.lastAddedServiceId)
        assertEquals("sepideh", report.lastAddedIdentifier)
        assertEquals(2, report.totalAccounts)
        assertEquals(2L, report.lastAddedAt)
    }
    @Test fun missingUsernameFallsBackToTitleAndSecretsNeverEnterReport() {
        val report = passwordHomeReport(listOf(Password(serviceName = "GitHub", title = "Projects", username = " ", secret = "private-password", notes = "private-notes")))
        assertEquals("Projects", report.lastAddedIdentifier)
        assertFalse(report.toString().contains("private-password"))
        assertFalse(report.toString().contains("private-notes"))
    }
    @Test fun noEntriesProducesAnEmptyReport() {
        val report = passwordHomeReport(emptyList())
        assertEquals(0, report.totalAccounts)
        assertNull(report.lastAddedAt)
        assertEquals("", report.lastAddedService)
        assertEquals("", report.lastAddedIdentifier)
    }
}
