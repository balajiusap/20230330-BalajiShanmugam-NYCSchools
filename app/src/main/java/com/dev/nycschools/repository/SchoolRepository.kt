package com.dev.nycschools.repository

import com.dev.nycschools.models.school.School
import com.dev.nycschools.models.school.SchoolSatScore
import com.dev.nycschools.network.login.APIService
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Repository for School Flow.
 * Requests data from either Network or DB.
 *
 */
class SchoolRepository : KoinComponent {

    private val mAPIService: APIService by inject()

    /**
     * Request NYC School data from backend
     */
    suspend fun getSchoolsData(limit: Int, offset: Int): ArrayList<School> {
        return processDataFetchSchools(limit, offset)
    }

    /**
     * Request School Sat details data from backend
     */
    suspend fun getSchoolSatScoreData(dbn: String): ArrayList<SchoolSatScore> {
        return processDataFetchSchoolsSatScoreData(dbn)
    }

    private suspend fun processDataFetchSchools(limit: Int, offset: Int): ArrayList<School> {
        return mAPIService.getSchoolsData(limit, offset)
    }

    private suspend fun processDataFetchSchoolsSatScoreData(dbn: String): ArrayList<SchoolSatScore> {
        return mAPIService.getSchoolSatScoreData(dbn)
    }


}