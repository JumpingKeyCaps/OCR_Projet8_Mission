package com.ocrmission.vitesse.data.repository

import com.ocrmission.vitesse.data.room.dao.CandidateDtoDao
import com.ocrmission.vitesse.data.room.entity.CandidateDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Repository for Candidate data
 */
class CandidateRepository @Inject constructor(private val CandidateDao: CandidateDtoDao) {

    /**
     * Method to get all candidates.
     * @return a flow of a list of CandidateDto.
     */
    fun getAllCandidates(): Flow<List<CandidateDto>>{
        return CandidateDao.getAllCandidates()
    }

    /**
     * Method to add a candidate.
     * @param candidate the candidate dto to be added.
     */
    suspend fun addCandidate(candidate: CandidateDto){
        CandidateDao.insertCandidate(candidate)

    }

    /**
     * Method to delete a candidate.
     * @param candidate the candidate dto to be deleted.
     */
    suspend fun deleteCandidate(candidate: CandidateDto){
        CandidateDao.deleteCandidate(candidate)
    }

    /**
     * Method to update a candidate profile.
     * @param candidate the candidate dto with updated information.
     * @return 1 if successful, 0 otherwise.
     */
    suspend fun updateCandidate(candidate: CandidateDto): Int {
        return CandidateDao.updateCandidate(candidate)
    }


}