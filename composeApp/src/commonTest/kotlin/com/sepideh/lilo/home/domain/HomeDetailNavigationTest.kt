package com.sepideh.lilo.home.domain

import com.sepideh.lilo.app.navigation.AppRoutes
import com.sepideh.lilo.app.navigation.detailRoute
import com.sepideh.lilo.note.domain.model.Note
import com.sepideh.lilo.password.domain.model.Password
import com.sepideh.lilo.task.domain.model.Task
import kotlin.test.*

class HomeDetailNavigationTest {
    @Test fun previewRoutesUseItemIdsRatherThanServiceOrGroupIds() {
        assertEquals(AppRoutes.Tasks.Detail(42), taskHomeReport(listOf(Task(id = 42, title = "Task"))).detailRoute())
        assertEquals(AppRoutes.Notes.Detail(17), noteHomeReport(listOf(Note(id = 17, title = "Note", groupId = 3))).detailRoute())
        assertEquals(AppRoutes.Passwords.Detail("entry"), passwordHomeReport(listOf(
            Password(id = "entry", serviceId = "github"))).detailRoute())
    }

    @Test fun emptyReportsCannotOpenNewItemForms() {
        assertNull(taskHomeReport(emptyList()).detailRoute())
        assertNull(noteHomeReport(emptyList()).detailRoute())
        assertNull(passwordHomeReport(emptyList()).detailRoute())
        assertNull(passwordHomeReport(listOf(Password(id = " "))).detailRoute())
    }
}
