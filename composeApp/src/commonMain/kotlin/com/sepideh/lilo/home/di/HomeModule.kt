package com.sepideh.lilo.home.di

import com.sepideh.lilo.home.domain.FeatureCard
import com.sepideh.lilo.home.domain.FeatureCardFactory
import com.sepideh.lilo.home.domain.NoteFeatureCardImpl
import com.sepideh.lilo.home.domain.TaskFeatureCardImpl
import com.sepideh.lilo.home.presentation.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val homeModule = module {

    single { TaskFeatureCardImpl() } bind FeatureCard::class
    single { NoteFeatureCardImpl() } bind FeatureCard::class


    single { FeatureCardFactory(getAll()) }

    viewModel {
        HomeViewModel(
            featureCardFactory = get()
        )
    }

}
