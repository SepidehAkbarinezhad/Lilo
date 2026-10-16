package com.sepideh.lilo.core.domain

import com.sepideh.lilo.core.domain.validation.*
import kotlin.test.*

class ShortNameTest {
    @Test fun acceptsBoundaryAndIgnoresOuterWhitespace() {
        for (name in listOf("a".repeat(20), "ن".repeat(20), "  " + "a".repeat(20) + "  ")) {
            assertFalse(isShortNameTooLong(name))
            requireShortNameLength(name)
        }
    }
    @Test fun rejectsOversizedNamesAtPersistenceBoundary() {
        for (name in listOf("a".repeat(21), "ن".repeat(21))) {
            assertTrue(isShortNameTooLong(name))
            assertFailsWith<IllegalArgumentException> { requireShortNameLength(name) }
        }
    }
    @Test fun unchangedLegacyTranslationRemainsUsableButNewLongNamesAreRejected() {
        val old = "a".repeat(30)
        requireShortNameLength(old, old)
        requireShortNameLength("Shorter", old)
        assertFailsWith<IllegalArgumentException> { requireShortNameLength(old + "x", old) }
    }
}
