package com.sepideh.lilo.task.data

import com.sepideh.lilo.task.data.mapper.toEntity
import com.sepideh.lilo.task.data.mapper.toTask
import com.sepideh.lilo.task.domain.model.Task
import kotlin.test.*

class TaskImagePersistenceTest {
    @Test fun attachmentsSurviveMappingAndCompletionEdits() {
        val task = Task(id = 4, title = "Practice", priority = 1, imageNames = listOf("a.jpg", "quote\".png"))
        assertEquals(task, task.toEntity().toTask())
        assertEquals(task.imageNames, task.copy(done = true).toEntity().toTask().imageNames)
        assertEquals(emptyList(), Task(title = "Old task").toEntity().toTask().imageNames)
    }

}
