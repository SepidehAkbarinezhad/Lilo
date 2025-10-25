package com.sepideh.lilo.task.data.category

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