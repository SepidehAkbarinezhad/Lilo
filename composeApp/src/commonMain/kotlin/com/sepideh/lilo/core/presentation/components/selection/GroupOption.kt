package com.sepideh.lilo.core.presentation.components.selection

/** UI values supplied by the feature that owns the groups. */
data class GroupOption(val id: Long, val label: String, val isDeletable: Boolean = true)
