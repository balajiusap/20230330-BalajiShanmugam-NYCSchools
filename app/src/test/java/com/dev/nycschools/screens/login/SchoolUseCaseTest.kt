package com.dev.nycschools.screens.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.dev.nycschools.base.BaseUTTest
import com.dev.nycschools.di.configureTestAppComponent
import com.dev.nycschools.network.login.APIService
import com.dev.nycschools.repository.SchoolRepository
import com.dev.nycschools.screens.school.SchoolUseCase
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.koin.core.context.startKoin
import org.koin.test.inject
import java.net.HttpURLConnection

@RunWith(JUnit4::class)
class SchoolUseCaseTest : BaseUTTest() {

    //Target
    private lateinit var mSchoolUseCase: SchoolUseCase

    //Inject login repo created with koin
    val mLoginRepo: SchoolRepository by inject()

    //Inject api service created with koin
    val mAPIService: APIService by inject()

    //Inject Mockwebserver created with koin
    val mockWebServer: MockWebServer by inject()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val mNextValue = "Clinton School Writers & Artists, M.S. 260"
    private val mParam = "32K549"
    private val mLimit = 50
    private val mOffset = 0
    private val mCount = 35

    @Before
    fun start() {
        super.setUp()
        //Start Koin with required dependencies
        startKoin { modules(configureTestAppComponent(getMockWebServerUrl())) }
    }

    @Test
    fun test_school_list_use_case_returns_expected_value() = runBlocking {

        mockNetworkResponseWithFileContent("success_resp_list.json", HttpURLConnection.HTTP_OK)
        mSchoolUseCase = SchoolUseCase()

        val dataReceived = mSchoolUseCase.processSchoolsUseCase(mLimit, mOffset)

        assertNotNull(dataReceived)
        assertEquals(dataReceived.size, mCount)
        assertEquals(dataReceived[0].schoolName, mNextValue)
    }

    @Test
    fun test_school_sat_details_use_case_returns_expected_value() = runBlocking {

        mockNetworkResponseWithFileContent("success_resp_sat.json", HttpURLConnection.HTTP_OK)
        mSchoolUseCase = SchoolUseCase()

        val dataReceived = mSchoolUseCase.processSchoolSatScoreUseCase(mParam)

        assertNotNull(dataReceived)
        assertEquals(dataReceived.size, 1)
        assertEquals(dataReceived[0].schoolName, mNextValue)
    }
}