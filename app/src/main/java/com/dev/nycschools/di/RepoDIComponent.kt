package com.dev.nycschools.di

import com.dev.nycschools.repository.SchoolRepository
import org.koin.dsl.module

/**
 * Repository DI module.
 * Provides Repo dependency.
 */
val RepoDependency = module {

    factory {
        SchoolRepository()
    }

}