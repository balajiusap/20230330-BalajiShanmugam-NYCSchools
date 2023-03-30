package com.dev.nycschools.screens.school

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dev.nycschools.models.school.School
import com.dev.nycschools.models.school.SchoolSatScore
import com.dev.nycschools.platform.LiveDataWrapper
import kotlinx.coroutines.*
import org.koin.core.KoinComponent

/**
 * School View Model.
 * Handles connecting with corresponding Use Case.
 * Getting back data to View.
 */

class SchoolActivityViewModel(
    mainDispatcher: CoroutineDispatcher,
    ioDispatcher: CoroutineDispatcher,
    private val useCase: SchoolUseCase
) : ViewModel(), KoinComponent {

    var mSchoolResponse = MutableLiveData<LiveDataWrapper<ArrayList<School>>>()
    var mSchoolSatScoreResponse = MutableLiveData<LiveDataWrapper<ArrayList<SchoolSatScore>>>()
    val mLoadingLiveData = MutableLiveData<Boolean>()
    private val job = SupervisorJob()
    private val mUiScope = CoroutineScope(mainDispatcher + job)
    private val mIoScope = CoroutineScope(ioDispatcher + job)
    private var mLimit = 50
    private var mOffset = 0

    init {
        //call reset to reset all VM data as required
        resetValues()
    }

    //Reset any member as per flow
    private fun resetValues() {

    }

    //can be called from View explicitly if required
    //Keep default scope
    fun requestLoginActivityData() {
        if (mSchoolResponse.value == null) {
            mUiScope.launch {
                mSchoolResponse.value = LiveDataWrapper.loading()
                setLoadingVisibility(true)
                try {
                    val data = mIoScope.async {
                        return@async useCase.processSchoolsUseCase(mLimit, mOffset)
                    }.await()
                    try {
                        mSchoolResponse.value = LiveDataWrapper.success(data)
                    } catch (_: Exception) {
                    }
                    setLoadingVisibility(false)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    mSchoolResponse.value = LiveDataWrapper.error(e)
                }
            }
        }
    }

    //can be called from View explicitly if required
    //Keep default scope
    fun requestLoadMoreData() {
        mUiScope.launch {
            mOffset += mLimit
            try {
                val data = mIoScope.async {
                    return@async useCase.processSchoolsUseCase(mLimit, mOffset)
                }.await()
                try {
                    mSchoolResponse.value = LiveDataWrapper.loadMore(data)
                } catch (_: Exception) {
                }
            } catch (_: Exception) {
            }
        }
    }

    //can be called from View explicitly if required
    //Keep default scope
    fun requestSchoolSatScoreData(dbn: String) {
        if (mSchoolSatScoreResponse.value == null) {
            mUiScope.launch {
                mSchoolSatScoreResponse.value = LiveDataWrapper.loading()
                setLoadingVisibility(true)
                try {
                    val data = mIoScope.async {
                        return@async useCase.processSchoolSatScoreUseCase(dbn)
                    }.await()
                    try {
                        mSchoolSatScoreResponse.value = LiveDataWrapper.success(data)
                    } catch (_: Exception) {
                    }
                    setLoadingVisibility(false)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    mSchoolSatScoreResponse.value = LiveDataWrapper.error(e)
                }
            }
        }
    }

    private fun setLoadingVisibility(visible: Boolean) {
        mLoadingLiveData.postValue(visible)
    }

    override fun onCleared() {
        super.onCleared()
        this.job.cancel()
    }
}