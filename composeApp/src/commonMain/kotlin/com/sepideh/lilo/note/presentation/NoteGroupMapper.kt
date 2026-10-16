package com.sepideh.lilo.note.presentation

import com.sepideh.lilo.core.domain.model.AppLanguage
import com.sepideh.lilo.core.presentation.components.group.GroupOption
import com.sepideh.lilo.core.domain.format.alphabeticalKey
import com.sepideh.lilo.note.domain.model.NoteGroup

fun List<NoteGroup>.toGroupOptions(language: AppLanguage): List<GroupOption> = map { group ->
    val label = when (language) {
        AppLanguage.FA -> group.titleFa.ifBlank { group.titleEn }
        AppLanguage.EN -> group.titleEn.ifBlank { group.titleFa }
    }
    GroupOption(group.id, label, isDeletable = true, isEditable = !group.isDefault)
}.filter { it.label.isNotBlank() }.sortedWith(compareBy<GroupOption> { alphabeticalKey(it.label) }.thenBy { it.id })
