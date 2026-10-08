package com.sepideh.lilo.task

import com.sepideh.lilo.task.data.local.room.toDomain
import com.sepideh.lilo.task.data.local.room.toEntity
import com.sepideh.lilo.task.domain.TaskGroup
import com.sepideh.lilo.task.presentation.toPresentationList
import com.sepideh.lilo.task.presentation.toPresentation
import com.sepideh.lilo.core.domain.model.AppLanguage
import kotlin.test.*
import com.sepideh.lilo.task.data.local.room.defaultTaskGroups

class GroupPolicyTest {
    @Test fun defaultsAreTranslatedAndDeleteOnly() {
        val group = TaskGroup(id = 7, titleEn = "Work", titleFa = "کار", isDefault = true)
        val persisted = group.toEntity().toDomain()
        assertEquals(group, persisted)
        for (language in AppLanguage.entries) {
            val shown = persisted.toPresentation(language)
            assertTrue(shown.isDeletable)
            assertFalse(shown.isEditable)
            assertEquals(if (language == AppLanguage.FA) "کار" else "Work", shown.title)
        }
    }

    @Test fun customNamesFallBackWhenOtherLanguageIsEmpty() {
        val group = TaskGroup(titleFa = "خرید")
        assertEquals("خرید", group.toPresentation(AppLanguage.EN).title)
        assertTrue(group.toPresentation(AppLanguage.FA).isEditable)
        assertFalse(group.toEntity().toDomain().isDefault)
        val renamed = group.copy(titleEn = "Shopping")
        assertEquals("خرید", renamed.toPresentation(AppLanguage.FA).title)
        assertEquals("Shopping", renamed.toPresentation(AppLanguage.EN).title)
    }

    @Test fun seededGroupsExcludeAllAndNoGroup() {
        val defaults = defaultTaskGroups
        assertEquals(listOf("Work", "Personal", "Shopping", "Health", "Hobby", "Music"), defaults.map { it.titleEn })
    }
    @Test fun groupsSortByVisibleLabelIncludingFallbackAndPersianLetters() {
        val english = listOf(TaskGroup(titleEn = "work"), TaskGroup(titleFa = "شخصی"), TaskGroup(titleEn = "Shopping"), TaskGroup(titleEn = "health"))
        assertEquals(listOf("health", "Shopping", "work", "شخصی"), english.toPresentationList(AppLanguage.EN).map { it.title })
        val persian = listOf("کار", "پروژه", "خرید", "بانک", "یادگیری").map { TaskGroup(titleFa = it) }
        assertEquals(listOf("بانک", "پروژه", "خرید", "کار", "یادگیری"), persian.toPresentationList(AppLanguage.FA).map { it.title })
    }
}
