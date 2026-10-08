package com.sepideh.lilo.task.data.local.room

import com.sepideh.lilo.task.domain.TaskGroup

fun TaskGroupEntity.toDomain(): TaskGroup = TaskGroup(
    id = id,
    titleEn = titleEn,
    titleFa = titleFa,
    isDefault = isDefault
)

fun List<TaskGroupEntity>.toDomainList() = this.map { it.toDomain() }

fun TaskGroup.toEntity(): TaskGroupEntity =
    TaskGroupEntity(id = id, titleFa = titleFa, titleEn = titleEn, isDefault = isDefault)
