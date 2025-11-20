package com.sepideh.lilo.category.data.local.room

import com.sepideh.lilo.task.presentation.model.Category

fun CategoryEntity.toCategory(): Category = Category(
    id = id,
    title = title,
    secondTitle=secondTitle

)

fun List<CategoryEntity>.toCategoryList() = this.map {
    it.toCategory()
}

fun Category.toEntity(): CategoryEntity = CategoryEntity(
    id = id,
    title = title,
    secondTitle=secondTitle
)