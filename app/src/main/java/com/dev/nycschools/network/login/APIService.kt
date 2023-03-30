package com.dev.nycschools.network.login

import com.dev.nycschools.models.school.School
import com.dev.nycschools.models.school.SchoolSatScore
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Login service Retrofit API.
 */
interface APIService {

    @GET("resource/s3k6-pzi2.json")
    suspend fun getSchoolsData(
        @Query("$" + "limit") limit: Int?,
        @Query("$" + "offset") offset: Int?
    ): ArrayList<School>


    @GET("resource/f9bf-2cp4.json")
    suspend fun getSchoolSatScoreData(@Query("dbn") dbn: String): ArrayList<SchoolSatScore>

}