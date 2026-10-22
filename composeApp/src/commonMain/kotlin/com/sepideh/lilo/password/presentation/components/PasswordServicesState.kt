package com.sepideh.lilo.password.presentation.components

import com.sepideh.lilo.password.domain.*

data class PasswordServicesState(
    val services: List<PasswordService> = emptyList(), val loaded: Boolean = false, val open: Boolean = false,
    val managing: Boolean = false, val draftId: String? = null, val query: String = "", val editorOpen: Boolean = false,
    val editingId: String? = null, val name: String = "", val busy: Boolean = false,
    val duplicateName: Boolean = false,
    val error: Boolean = false, val deleting: PasswordService? = null,
    val removedId: String? = null, val movedToId: String? = null,
    val usage: Int = 0, val replacement: String? = null,
)
