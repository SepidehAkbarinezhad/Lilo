package com.sepideh.lilo.task.domain

import com.sepideh.lilo.task.domain.reminder.Reminder
import com.sepideh.lilo.task.domain.reminder.ReminderScheduler
import com.sepideh.lilo.task.domain.reminder.RepeatRule
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.*

class ReminderSchedulerTest {
    private val reminders = (1L..4L).map {
        Reminder(it, "Private title", "Private content", 2_000_000_000_000L, RepeatRule.DAILY, "UTC")
    }

    @Test fun emptyListDoesNothing() = runTest {
        val scheduler = Scheduler()
        scheduler.synchronize(emptyList())
        assertTrue(scheduler.attempted.isEmpty())
    }

    @Test fun successfulBatchSchedulesEveryReminder() = runTest {
        val scheduler = Scheduler()
        scheduler.synchronize(reminders)
        assertEquals(listOf(1L, 2L, 3L, 4L), scheduler.scheduled)
    }

    private suspend fun verifyIsolation(failedId: Long) {
        val failure = IllegalStateException("Injected scheduling failure")
        val scheduler = Scheduler(mapOf(failedId to failure))
        val thrown = assertFailsWith<IllegalStateException> { scheduler.synchronize(reminders) }
        assertSame(failure, thrown, "Failure must still reach the caller after the batch")
        assertEquals(listOf(1L, 2L, 3L, 4L), scheduler.attempted)
        assertEquals(listOf(1L, 2L, 3L, 4L) - failedId, scheduler.scheduled)
    }

    @Test fun firstFailureDoesNotBlockRemainingReminders() = runTest { verifyIsolation(1) }
    @Test fun middleFailureDoesNotBlockRemainingReminders() = runTest { verifyIsolation(2) }
    @Test fun lastFailurePreservesEarlierSuccessfulScheduling() = runTest { verifyIsolation(4) }

    @Test fun multipleFailuresAreReportedAfterHealthyRemindersAreScheduled() = runTest {
        val first = IllegalStateException("First")
        val second = IllegalArgumentException("Second")
        val scheduler = Scheduler(mapOf(1L to first, 3L to second))
        val thrown = assertFailsWith<IllegalStateException> { scheduler.synchronize(reminders) }
        assertSame(first, thrown)
        assertEquals(listOf(second), thrown.suppressedExceptions)
        assertEquals(listOf(1L, 2L, 3L, 4L), scheduler.attempted)
        assertEquals(listOf(2L, 4L), scheduler.scheduled)
    }

    @Test fun reusedExceptionDoesNotCauseSelfSuppressionOrStopBatch() = runTest {
        val failure = IllegalStateException("Same exception instance")
        val scheduler = Scheduler(mapOf(1L to failure, 2L to failure))
        assertSame(failure, assertFailsWith<IllegalStateException> { scheduler.synchronize(reminders) })
        assertEquals(listOf(3L, 4L), scheduler.scheduled)
        assertTrue(failure.suppressedExceptions.isEmpty())
    }

    @Test fun cancellationStopsImmediatelyEvenAfterOrdinaryFailure() = runTest {
        val cancelled = CancellationException("Stop restoration")
        val scheduler = Scheduler(mapOf(1L to IllegalStateException("Earlier failure"), 2L to cancelled))
        assertSame(cancelled, assertFailsWith<CancellationException> { scheduler.synchronize(reminders) })
        assertEquals(listOf(1L, 2L), scheduler.attempted)
        assertTrue(scheduler.scheduled.isEmpty())
    }

    @Test fun eachFailureIsLoggedWithoutTaskContentOrExceptionMessage() = runTest {
        val scheduler = Scheduler(mapOf(1L to IllegalStateException("Private error details"),
            3L to IllegalArgumentException("Private error details")))
        val original = System.out
        val bytes = ByteArrayOutputStream()
        val output = PrintStream(bytes, true, "UTF-8")
        try {
            System.setOut(output)
            assertFailsWith<IllegalStateException> { scheduler.synchronize(reminders) }
        } finally {
            System.setOut(original)
            output.close()
        }
        val log = bytes.toString("UTF-8")
        assertContains(log, "task 1: IllegalStateException")
        assertContains(log, "task 3: IllegalArgumentException")
        assertFalse(log.contains("Private"))
    }

    /** Inherits production synchronize(); only individual OS scheduling calls are replaced. */
    private class Scheduler(private val failures: Map<Long, Exception> = emptyMap()) : ReminderScheduler {
        val attempted = mutableListOf<Long>()
        val scheduled = mutableListOf<Long>()
        override suspend fun scheduleReminder(reminder: Reminder) {
            attempted += reminder.id
            failures[reminder.id]?.let { throw it }
            scheduled += reminder.id
        }
        override suspend fun cancelReminder(taskId: Long) = Unit
    }
}
