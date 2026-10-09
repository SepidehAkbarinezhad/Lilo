package com.sepideh.lilo.note.domain.model

import com.sepideh.lilo.core.domain.model.AppLanguage

data class NoteGroup(val id: Long = 0, val titleEn: String = "", val titleFa: String = "", val isDefault: Boolean = false) {
    fun renamed(title: String, language: AppLanguage): NoteGroup {
        require(!isDefault) { "Default groups are delete-only" }
        require(title.isNotBlank())
        return when (language) {
            AppLanguage.FA -> copy(titleFa = title.trim())
            AppLanguage.EN -> copy(titleEn = title.trim())
        }
    }
    companion object {
        fun create(title: String, language: AppLanguage): NoteGroup {
            require(title.isNotBlank())
            return when (language) {
                AppLanguage.FA -> NoteGroup(titleFa = title.trim())
                AppLanguage.EN -> NoteGroup(titleEn = title.trim())
            }
        }
    }
}
