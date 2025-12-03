package com.sepideh.lilo.category.domain.model

data class Category(
    val id: Long = 0,
    val titleEn: String = "",
    val titleFa: String = ""
) {
    companion object {
        val categories =
            listOf(
                Category(titleEn = "all", titleFa = "همه"),
                Category(titleEn = "general", titleFa = "عمومی"),
                Category(titleEn = "work", titleFa = "کار"),
                Category(titleEn = "hobby", titleFa = "سرگرمی"),
                Category(titleEn = "music", titleFa = "موسیقی")
            )
    }
}