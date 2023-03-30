package com.dev.nycschools.screens.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.dev.nycschools.base.BaseUTTest
import com.dev.nycschools.di.configureTestAppComponent
import com.dev.nycschools.models.school.School
import com.dev.nycschools.models.school.SchoolSatScore
import com.dev.nycschools.platform.LiveDataWrapper
import com.dev.nycschools.screens.school.SchoolActivityViewModel
import com.dev.nycschools.screens.school.SchoolUseCase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.koin.core.context.startKoin

@RunWith(JUnit4::class)
class SchoolActivityViewModelTest : BaseUTTest() {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    lateinit var mSchoolActivityViewModel: SchoolActivityViewModel

    val mDispatcher = Dispatchers.Unconfined

    @MockK
    lateinit var mSchoolUseCase: SchoolUseCase

    private val mNextValue = "Clinton School Writers & Artists, M.S. 260"
    private val mParam = "32K549"
    private val mLimit = 50
    private val mOffset = 0
    private val mCount = 35

    @Before
    fun start() {
        super.setUp()
        //Used for initiation of Mockk
        MockKAnnotations.init(this)
        //Start Koin with required dependencies
        startKoin { modules(configureTestAppComponent(getMockWebServerUrl())) }
    }

    @Test
    fun test_school_list_view_model_data_populates_expected_value() {

        mSchoolActivityViewModel = SchoolActivityViewModel(mDispatcher, mDispatcher, mSchoolUseCase)
        val sampleResponse = getJson("success_resp_list.json")
        val myType = object : TypeToken<ArrayList<School>>() {}.type
        var jsonObj = Gson().fromJson<ArrayList<School>>(sampleResponse, myType)
        //Make sure login use case returns expected response when called
        coEvery { mSchoolUseCase.processSchoolsUseCase(mLimit, mOffset) } returns jsonObj
        mSchoolActivityViewModel.mSchoolResponse.observeForever { }

        mSchoolActivityViewModel.requestLoginActivityData()

        assert(mSchoolActivityViewModel.mSchoolResponse.value != null)
        assert(
            mSchoolActivityViewModel.mSchoolResponse.value!!.responseStatus
                    == LiveDataWrapper.RESPONSESTATUS.SUCCESS
        )
        val testResult =
            mSchoolActivityViewModel.mSchoolResponse.value as LiveDataWrapper<ArrayList<School>>
        assertEquals(testResult.response!![0].schoolName, mNextValue)
    }

    @Test
    fun test_school_sat_details_view_model_data_populates_expected_value() {

        mSchoolActivityViewModel = SchoolActivityViewModel(mDispatcher, mDispatcher, mSchoolUseCase)
        val sampleResponse = getJson("success_resp_sat.json")
        val myType = object : TypeToken<ArrayList<SchoolSatScore>>() {}.type
        var jsonObj = Gson().fromJson<ArrayList<SchoolSatScore>>(sampleResponse, myType)
        //Make sure login use case returns expected response when called
        coEvery { mSchoolUseCase.processSchoolSatScoreUseCase(mParam) } returns jsonObj
        mSchoolActivityViewModel.mSchoolSatScoreResponse.observeForever { }

        mSchoolActivityViewModel.requestSchoolSatScoreData(mParam)

        assert(mSchoolActivityViewModel.mSchoolSatScoreResponse.value != null)
        assert(
            mSchoolActivityViewModel.mSchoolSatScoreResponse.value!!.responseStatus
                    == LiveDataWrapper.RESPONSESTATUS.SUCCESS
        )
        val testResult =
            mSchoolActivityViewModel.mSchoolSatScoreResponse.value as LiveDataWrapper<ArrayList<SchoolSatScore>>
        assertEquals(testResult.response!![0].schoolName, mNextValue)
    }
}