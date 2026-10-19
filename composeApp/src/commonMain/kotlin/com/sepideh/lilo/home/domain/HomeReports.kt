package com.sepideh.lilo.home.domain

import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.note.domain.model.Note
import com.sepideh.lilo.home.presentation.model.*

internal fun taskHomeReport(tasks: List<Task>): TaskReportDetail {
    val selected = tasks.maxWithOrNull(compareBy<Task> { it.createdAt }.thenBy { it.id ?: Long.MIN_VALUE })
    return TaskReportDetail(selected?.title, null, tasks.count { !it.done }, tasks.size,
        selected?.id, selected?.priority, selected?.done == true, selected?.description.orEmpty())
}

internal fun noteHomeReport(notes: List<Note>): NoteReportDetail {
    val latest = notes.maxByOrNull { it.lastModifiedAt }
    return NoteReportDetail(latest?.title.orEmpty(), latest?.content.orEmpty(), notes.size, null, notes.size, latest?.lastModifiedAt, latest?.id)
}
