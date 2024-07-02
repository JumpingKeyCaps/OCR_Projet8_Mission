package com.ocrmission.vitesse.data.repository

import com.ocrmission.vitesse.data.room.dao.CandidateDtoDao
import com.ocrmission.vitesse.data.room.entity.CandidateDto
import junit.framework.TestCase
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

/**
 * Unit tests for the CandidateRepository class.
 */
@RunWith(JUnit4::class)
class CandidateRepositoryTest {

    @Mock
    private lateinit var candidateDao: CandidateDtoDao
    private lateinit var candidateRepository: CandidateRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this) // Initialize mocks
        candidateRepository = CandidateRepository(candidateDao) // Initialize repository
    }

    /**
     * Test the getAllCandidates method behavior of the CandidateRepository class.
     */
    @Test
    fun test_getAllCandidates_candidateRepositoryEmitsCandidates_whenDaoReturnsCandidatesFlow() = runBlocking  {
        // Mock the dao behavior
        val expectedCandidatesList = listOf(
            CandidateDto(1, "John", "Doe", "test1@test.test", "0602020202",0,10000,"","",false),
            CandidateDto(2, "Bob", "Toe", "test2@test.test", "0602020202",0,20000,"","",false),
            CandidateDto(3, "Marie", "Foe", "test3@test.test", "0603030303",0,30000,"","",false)
        )
        val candidateFlow = flow { emit(expectedCandidatesList) }
        Mockito.`when`(candidateDao.getAllCandidates()).thenReturn(candidateFlow)
        // ACT
        val result = candidateRepository.getAllCandidates()
        // Assert
        TestCase.assertNotNull(result)
        TestCase.assertEquals(candidateFlow, result)
        //test flow value integrity
        result.collect { Dtos ->
            val candidates = Dtos.map { it }
            TestCase.assertEquals(expectedCandidatesList, candidates)
        }
    }

    /**
     * Test the getCandidateById method behavior of the CandidateRepository class.
     */
    @Test
    fun test_getCandidateById_candidateRepository_emitsCandidate_whenDaoReturnsCandidateFlow() = runBlocking {
        // Mock DAO behavior
        val expectedCandidate = CandidateDto(1, "John", "Doe", "test1@test.test", "0602020202", 0, 10000, "", "", false)
        val candidateFlow = flowOf(expectedCandidate)
        Mockito.`when`(candidateDao.getCandidatesById(expectedCandidate.id)).thenReturn(candidateFlow)

        // Act
        val result = candidateRepository.getCandidateById(expectedCandidate.id)

        // Assert
        TestCase.assertNotNull(result)

        // Verify the flow content
        result.collect { candidate ->
            TestCase.assertNotNull(candidate) // Assert retrieved candidate is not null
            TestCase.assertEquals(expectedCandidate, candidate)
        }
    }

    /**
     * Test the addCandidate method behavior of the CandidateRepository class.
     */
    @Test
    fun test_addCandidate_CandidateRepository_addCandidateWithCandidateDao() = runTest {
        // Prepare test data
        val newCandidate = CandidateDto( 10,"Jane","Doe","test4@test.test","0604040404",0,15000,"","",false)
        Mockito.`when`(candidateDao.insertCandidate(newCandidate)).thenReturn(1L)
        // Act
        val addResult = candidateRepository.addCandidate(newCandidate)
        assertEquals(Unit, addResult)
        // Assert (verify interaction with DAO)
        Mockito.verify(candidateDao).insertCandidate(newCandidate)
    }


    /**
     * Test the deleteCandidate method behavior of the CandidateRepository class.
     */
    @Test
    fun test_deleteCandidate_CandidateRepository_deleteCandidateWithCandidateDao() = runTest {
        // Similar to addCandidate, mocking DAO behavior directly might be tricky.
        // Prepare test data
        val candidateToDelete = CandidateDto(10, "Jane", "Doe", "test4@test.test", "0604040404", 0, 15000, "", "", false)
        Mockito.`when`(candidateDao.deleteCandidate(candidateToDelete)).thenReturn(Unit)
        // Act
        val deleteResult = candidateRepository.deleteCandidate(candidateToDelete)
        assertEquals(Unit, deleteResult)
        // Assert (verify interaction with DAO, optional)
         Mockito.verify(candidateDao).deleteCandidate(candidateToDelete)
    }


    /**
     * Test the updateCandidate method behavior of the CandidateRepository class.
     */
    @Test
    fun test_UpdateCandidate_CandidateRepository_updateCandidateWithCandidateDao() = runTest {
        // Prepare test data
        val originalCandidate = CandidateDto(10, "Jane", "Doe", "test4@test.test", "0604040404", 0, 15000, "", "", false)
        val updatedCandidate = originalCandidate.copy(firstname = "Updated Name")
        Mockito.`when`(candidateDao.updateCandidate(updatedCandidate)).thenReturn(1)
        // Act
        val updateResult = candidateRepository.updateCandidate(updatedCandidate)
        assertEquals(1, updateResult)
        // Assert
        Mockito.verify(candidateDao).updateCandidate(updatedCandidate)
    }


}