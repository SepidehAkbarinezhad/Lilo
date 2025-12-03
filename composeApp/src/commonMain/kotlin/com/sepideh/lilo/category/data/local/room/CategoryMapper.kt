package com.sepideh.lilo.category.data.local.room

import com.sepideh.lilo.category.domain.model.Category

fun CategoryEntity.toCategory(): Category = Category(
    id = id,
    titleEn = titleEn,
    titleFa = titleFa
)

fun List<CategoryEntity>.toCategoryList() = this.map { it.toCategory() }

fun Category.toEntity(): CategoryEntity =
    CategoryEntity(id = id, titleFa = titleFa, titleEn = titleEn)
