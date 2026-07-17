package com.sepideh.lilo.home.di

import com.sepideh.lilo.home.domain.FeatureCard
import com.sepideh.lilo.home.domain.FeatureCardFactory
import com.sepideh.lilo.home.domain.TaskFeatureCardImpl
import org.koin.dsl.bind
import org.koin.dsl.module

val homeModule = module {

    single { TaskFeatureCardImpl(get()) } bind FeatureCard::class


    single { FeatureCardFactory(getAll()) }

}