package com.ocrmission.vitesse.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ocrmission.vitesse.data.room.entity.CandidateDto
import kotlinx.coroutines.flow.Flow


/**
 * Data Access Object (DAO) interface of the Candidate dto.
 *  regroup all CRUD actions for candidate table.
 */
@Dao
interface CandidateDtoDao {

    /**
     * Method to INSERT a new Candidate.
     * @param candidate the candidate dto to insert.
     * @return represents the primary key of the inserted candidate row (auto-gen by Room)
     */
    @Insert
    suspend fun insertCandidate(candidate: CandidateDto): Long

    /**
     * Method to DELETE a Candidate.
     * @param candidate the candidate dto to delete.
     */
    @Delete
    suspend fun deleteCandidate(candidate: CandidateDto)

    /**
     * Method to DELETE a Candidate by ID.
     * @param candidateId the id of the candidate to delete.
     * @return the number of rows affected (should be 1 for a successful delete)
     */
    @Query("DELETE FROM candidates WHERE id = :candidateId")
    suspend fun deleteCandidateById(candidateId: Long):Int



    /**
     * Method to GET all candidates
     * @return a Flow of the CandidateDto List
     */
    @Query("SELECT * FROM candidates")
    fun getAllCandidates(): Flow<List<CandidateDto>>

    /**
     * Method to GET a candidate by is ID
     * @param candidateId the id of the candidate to get
     * @return a Flow of the CandidateDto
     */
    @Query("SELECT * FROM candidates WHERE id = :candidateId")
    fun getCandidatesById(candidateId: Long): Flow<CandidateDto?>


    /**
     * Method to UPDATE a Candidate.
     * @param candidate the candidate dto with updated information.
     * @return the number of rows affected (should be 1 for a successful update)
     */
    @Update
    suspend fun updateCandidate(candidate: CandidateDto): Int


    /**
     * Method to UPDATE the favorite state of a Candidate.
     * @param candidateId the id of the candidate to update
     * @param newFavoriteState the new favorite state of the candidate
     * @return the number of rows affected (should be 1 for a successful update)
     */
    @Query("UPDATE candidates SET isFavorite = :newFavoriteState WHERE id = :candidateId")
    suspend fun updateFavoriteState(candidateId: Long, newFavoriteState: Boolean): Int


}