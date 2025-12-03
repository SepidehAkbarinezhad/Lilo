package com.sepideh.lilo.category.domain

data class CategoryDomain(
    val id: Long = 0,
    val titleEn: String = "",
    val titleFa: String = ""
) {
    companion object {
        val categories =
            listOf(
                CategoryDomain(titleEn = "all", titleFa = "همه"),
                CategoryDomain(titleEn = "general", titleFa = "عمومی"),
                CategoryDomain(titleEn = "work", titleFa = "کار"),
                CategoryDomain(titleEn = "hobby", titleFa = "سرگرمی"),
                CategoryDomain(titleEn = "music", titleFa = "موسیقی")
            )

        val first = categories.first()
    }
}