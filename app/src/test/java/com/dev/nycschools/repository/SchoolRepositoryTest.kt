package com.dev.nycschools.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.dev.nycschools.base.BaseUTTest
import com.dev.nycschools.di.configureTestAppComponent
import com.dev.nycschools.network.login.APIService
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.koin.core.context.startKoin
import org.koin.test.inject
import java.net.HttpURLConnection

@RunWith(JUnit4::class)
class SchoolRepositoryTest : BaseUTTest() {

    //Target
    private lateinit var mRepo: SchoolRepository

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

        startKoin { modules(configureTestAppComponent(getMockWebServerUrl())) }
    }

    @Test
    fun test_school_list_repo_retrieves_expected_data() = runBlocking<Unit> {

        mockNetworkResponseWithFileContent("success_resp_list.json", HttpURLConnection.HTTP_OK)
        mRepo = SchoolRepository()

        val dataReceived = mRepo.getSchoolsData(mLimit, mOffset)

        assertNotNull(dataReceived)
        assertEquals(dataReceived.size, mCount)
        assertEquals(dataReceived[0].schoolName, mNextValue)
    }


    @Test
    fun test_school_sat_details_repo_retrieves_expected_data() = runBlocking<Unit> {

        mockNetworkResponseWithFileContent("success_resp_sat.json", HttpURLConnection.HTTP_OK)
        mRepo = SchoolRepository()

        val dataReceived = mRepo.getSchoolSatScoreData(mParam)

        assertNotNull(dataReceived)
        assertEquals(dataReceived.size, 1)
        assertEquals(dataReceived[0].schoolName, mNextValue)
        assertEquals(dataReceived[0].numOfSatTestTakers, "75")
    }
}