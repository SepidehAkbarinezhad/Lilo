package com.sepideh.lilo.task.domain.model

enum class TaskSort { PRIORITY, REMINDER_DATE }

data class TaskQuery(
    val text: String = "",
    val groupId: Long? = null,
    val completed: Set<Boolean> = emptySet(),
    val priorities: Set<TaskPriority> = emptySet(),
    val sort: TaskSort = TaskSort.PRIORITY,
)

/** An empty selection means no restriction, including after Reset. */
fun List<Task>.matching(query: TaskQuery): List<Task> {
    val text = query.text.trim()
    val filtered = filter { task ->
        (query.groupId == null || task.category == query.groupId) &&
            (query.completed.isEmpty() || task.done in query.completed) &&
            (query.priorities.isEmpty() || TaskPriority.fromId(task.priority) in query.priorities) &&
            (text.isEmpty() || task.title.contains(text, true) || task.description.contains(text, true))
    }
    return when (query.sort) {
        TaskSort.PRIORITY -> filtered.sortedWith(compareBy<Task> { TaskPriority.fromId(it.priority).rank }.thenByDescending { it.createdAt }.thenBy { it.id })
        TaskSort.REMINDER_DATE -> filtered.sortedWith(compareBy<Task> { it.reminderStartDate ?: Long.MAX_VALUE }.thenBy { it.reminderHour ?: 0 }.thenBy { it.reminderMinute ?: 0 }.thenBy { it.id })
    }
}
