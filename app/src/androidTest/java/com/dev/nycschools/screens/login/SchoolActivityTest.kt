package com.dev.nycschools.screens.login

import android.os.SystemClock
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.dev.nycschools.R
import com.dev.nycschools.base.BaseUITest
import com.dev.nycschools.di.generateTestAppComponent
import com.dev.nycschools.helpers.recyclerItemAtPosition
import com.dev.nycschools.screens.school.SchoolActivity
import com.dev.nycschools.screens.school.SchoolRecyclerViewAdapter
import com.dev.nycschools.screens.school.SchoolUseCase
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.test.inject
import java.net.HttpURLConnection

@RunWith(AndroidJUnit4::class)
class SchoolActivityTest : BaseUITest() {

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(SchoolActivity::class.java, true, false)

    //Inject login use case created with koin
    val mSchoolUseCase: SchoolUseCase by inject()

    //Inject Mockwebserver created with koin
    val mMockWebServer: MockWebServer by inject()

    private val mSchoolName1 = "Clinton School Writers & Artists, M.S. 260"
    val mSchoolName9 = "Richmond Hill High School"


    @Before
    fun start() {
        super.setUp()
        loadKoinModules(generateTestAppComponent(getMockWebServerUrl()).toMutableList())
    }

    @Test
    fun test_recyclerview_elements_for_expected_response() {
        mActivityTestRule.launchActivity(null)

        mockNetworkResponseWithFileContent("success_resp_list.json", HttpURLConnection.HTTP_OK)

        //Wait for MockWebServer to get back with response
        SystemClock.sleep(5000)

        //Check if item at 0th position is having 0th element in json
        onView(withId(R.id.landingListRecyclerView))
            .check(
                matches(
                    recyclerItemAtPosition(
                        0,
                        ViewMatchers.hasDescendant(withText(mSchoolName1))
                    )
                )
            )


        //Scroll to last index in json
        onView(withId(R.id.landingListRecyclerView)).perform(
            RecyclerViewActions.scrollToPosition<SchoolRecyclerViewAdapter.LoginFragViewHolder>(9)
        )

        //Check if item at 9th position is having 9th element in json
        onView(withId(R.id.landingListRecyclerView))
            .check(
                matches(
                    recyclerItemAtPosition(
                        9,
                        ViewMatchers.hasDescendant(withText(mSchoolName9))
                    )
                )
            )

//        onView(withId(R.id.landingListRecyclerView)).perform(
//            RecyclerViewActions.actionOnItem(hasDescendant(withText(mSchoolName9)), click()));
    }
}