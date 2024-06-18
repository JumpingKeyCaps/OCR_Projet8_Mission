package com.ocrmission.vitesse.data.repository

import com.ocrmission.vitesse.data.room.dao.CandidateDtoDao
import com.ocrmission.vitesse.data.room.entity.CandidateDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Repository for Candidate data
 */
class CandidateRepository @Inject constructor(private val candidateDao: CandidateDtoDao) {

    /**
     * Method to get all candidates.
     * @return a flow of a list of CandidateDto.
     */
    fun getAllCandidates(): Flow<List<CandidateDto>>{
        return candidateDao.getAllCandidates()
    }

    /**
     * Method to get all candidates.
     * @param candidateId the id of the candidate to be retrieved.
     * @return a flow of a list of CandidateDto.
     */
    fun getCandidateById(candidateId: Long): Flow<CandidateDto?>{
        return candidateDao.getCandidatesById(candidateId)
    }


    /**
     * Method to add a candidate.
     * @param candidate the candidate dto to be added.
     */
    suspend fun addCandidate(candidate: CandidateDto){
        candidateDao.insertCandidate(candidate)

    }

    /**
     * Method to delete a candidate.
     * @param candidate the candidate dto to be deleted.
     */
    suspend fun deleteCandidate(candidate: CandidateDto){
        candidateDao.deleteCandidate(candidate)
    }
    /**
     * Method to delete a candidate by id.
     * @param candidateId the id of the candidate to be deleted.
     * @return 1 if successful, 0 otherwise.
     */
    suspend fun deleteCandidateById(candidateId: Long): Int{
        return candidateDao.deleteCandidateById(candidateId)
    }



    /**
     * Method to update a candidate profile.
     * @param candidate the candidate dto with updated information.
     * @return 1 if successful, 0 otherwise.
     */
    suspend fun updateCandidate(candidate: CandidateDto): Int {
        return candidateDao.updateCandidate(candidate)
    }

    /**
     * Method to update the favorite state of a candidate.
     * @param candidateId the id of the candidate to be updated.
     * @param newFavoriteState the new favorite state of the candidate.
     * @return 1 if successful, 0 otherwise.
     */
    suspend fun updateFavoriteState(candidateId: Long, newFavoriteState: Boolean): Int {
        return candidateDao.updateFavoriteState(candidateId, newFavoriteState)
    }

}