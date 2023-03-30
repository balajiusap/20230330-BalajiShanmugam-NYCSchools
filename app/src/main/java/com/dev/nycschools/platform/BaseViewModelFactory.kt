package com.dev.nycschools.platform

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dev.nycschools.screens.school.SchoolActivityViewModel
import com.dev.nycschools.screens.school.SchoolUseCase
import kotlinx.coroutines.CoroutineDispatcher

/**
 * Base VM Factory for creation of different VM's
 */
class BaseViewModelFactory constructor(
    private val mainDispather: CoroutineDispatcher,
    private val ioDispatcher: CoroutineDispatcher,
    private val useCase: SchoolUseCase
) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SchoolActivityViewModel::class.java)) {
            SchoolActivityViewModel(mainDispather, ioDispatcher, useCase) as T
        } else {
            throw IllegalArgumentException("ViewModel Not configured")
        }
    }
}