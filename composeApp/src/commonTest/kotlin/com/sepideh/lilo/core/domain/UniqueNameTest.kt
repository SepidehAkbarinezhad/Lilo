package com.sepideh.lilo.core.domain

import com.sepideh.lilo.core.domain.validation.*
import kotlin.test.*

class UniqueNameTest {
    @Test fun exactCaseAndOuterWhitespaceDuplicatesAreRejected() {
        for (name in listOf("Work", "work", " Work ")) {
            assertFailsWith<DuplicateNameException> { requireUniqueName(name, listOf("Work")) }
        }
    }
    @Test fun persianNamesAreCheckedAndDifferentNamesAreAllowed() {
        assertFailsWith<DuplicateNameException> { requireUniqueName(" شخصی ", listOf("شخصی")) }
        requireUniqueName("خرید", listOf("شخصی"))
        requireUniqueName("Workshop", listOf("Work"))
    }
}
