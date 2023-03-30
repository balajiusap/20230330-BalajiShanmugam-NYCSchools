package com.dev.nycschools.di

import com.dev.nycschools.screens.school.SchoolUseCase
import org.koin.dsl.module

/**
 * Use case DI module.
 * Provide Use case dependency.
 */
val UseCaseDependency = module {

    factory {
        SchoolUseCase()
    }
}