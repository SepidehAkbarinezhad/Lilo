package com.sepideh.lilo.category.data.local.room

import com.sepideh.lilo.category.domain.CategoryDomain

fun CategoryEntity.toDomain(): CategoryDomain = CategoryDomain(
    id = id,
    titleEn = titleEn,
    titleFa = titleFa
)

fun List<CategoryEntity>.toDomainList() = this.map { it.toDomain() }

fun CategoryDomain.toEntity(): CategoryEntity =
    CategoryEntity(id = id, titleFa = titleFa, titleEn = titleEn)
