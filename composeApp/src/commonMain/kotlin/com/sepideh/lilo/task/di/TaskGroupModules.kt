package com.sepideh.lilo.task.di
import com.sepideh.lilo.task.data.local.room.TaskDatabase
import com.sepideh.lilo.task.data.repository.TaskGroupRepositoryImpl
import com.sepideh.lilo.task.domain.TaskGroupFactory
import com.sepideh.lilo.task.domain.repository.TaskGroupRepository
import org.koin.dsl.module

val taskGroupModule = module {
    single { TaskGroupFactory(languageProvider = get()) }
    single { get<TaskDatabase>(taskDatabaseQualifier).taskGroupDao() }
    single<TaskGroupRepository> { TaskGroupRepositoryImpl(taskGroupDao = get()) }

}
