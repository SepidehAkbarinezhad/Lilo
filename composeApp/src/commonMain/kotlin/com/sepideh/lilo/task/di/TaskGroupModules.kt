package com.sepideh.lilo.task.di
import com.sepideh.lilo.task.data.local.room.TaskGroupDatabase
import com.sepideh.lilo.task.data.repository.TaskGroupRepositoryImpl
import com.sepideh.lilo.task.domain.TaskGroupFactory
import com.sepideh.lilo.task.domain.repository.TaskGroupRepository
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

val taskGroupDatabaseQualifier = named("taskGroupDatabase")

val taskGroupModule = module {
    single { TaskGroupFactory(languageProvider = get()) }
    single { get<TaskGroupDatabase>(taskGroupDatabaseQualifier).taskGroupDao() }
    single<TaskGroupRepository> { TaskGroupRepositoryImpl(taskGroupDao = get()) }

}

expect fun taskGroupPlatformModule(): Module

