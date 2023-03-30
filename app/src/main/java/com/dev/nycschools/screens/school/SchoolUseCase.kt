package com.dev.nycschools.screens.school

import com.dev.nycschools.models.school.School
import com.dev.nycschools.models.school.SchoolSatScore
import com.dev.nycschools.repository.SchoolRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Use Case class for handling School flow.
 * Requests data from Repo.
 * Process received data into required model and reverts back to ViewModel.
 */
class SchoolUseCase : KoinComponent {

    private val mSchoolRepo: SchoolRepository by inject()

    suspend fun processSchoolsUseCase(limit: Int, offset: Int): ArrayList<School> {
        return mSchoolRepo.getSchoolsData(limit, offset)
    }

    suspend fun processSchoolSatScoreUseCase(dbn: String): ArrayList<SchoolSatScore> {
        return mSchoolRepo.getSchoolSatScoreData(dbn)
    }
}