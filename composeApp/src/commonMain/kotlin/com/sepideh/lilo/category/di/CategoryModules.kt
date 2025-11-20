package com.sepideh.lilo.category.di

import org.koin.core.module.Module
import org.koin.core.qualifier.named


val categoryDatabaseQualifier = named("categoryDatabase")

expect fun categoryPlatformModule(): Module

