package com.ocrmission.vitesse.ui.viewmodel

import com.ocrmission.vitesse.data.repository.CandidateRepository
import com.ocrmission.vitesse.domain.Candidate
import com.ocrmission.vitesse.ui.editCandidate.EditCandidateViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

/**
 * Unit tests for the EditCandidateViewModel
 */
@RunWith(JUnit4::class)
class EditCandidateViewModelTest {

    @Mock
    private lateinit var candidateRepository: CandidateRepository

    private lateinit var viewModel: EditCandidateViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        viewModel = EditCandidateViewModel(candidateRepository)
    }


    /**
     * Test the fetchingCandidateById method in the ViewModel
     */
    @Test
    fun test_fetchingCandidateById_fetchesCandidate_andUpdatesCandidateFlow(): Unit = runBlocking  {
        val expectedCandidateId = 0L
        val expectedCandidateDto = Candidate.createDefaultCandidate().toDtoWithId()
        val expectedCandidate = Candidate.fromDto(expectedCandidateDto)

        // Mock the repository to return the expected candidate
        Mockito.`when`(candidateRepository.getCandidateById(expectedCandidateId))
            .thenReturn(flowOf(expectedCandidateDto))

        // Call the method in the ViewModel
        viewModel.fetchingCandidateById(expectedCandidateId)
        assertEquals(expectedCandidate, viewModel.candidate.value)

    }

    /**
     * Test the fetchingCandidateById method in the ViewModel with an error
     */
    @Test
    fun test_fetchingCandidateById_handlesError_emitsEmptyFlow(): Unit = runBlocking {
        val expectedCandidateId = 0L
        // Mock the repository to return an error
        Mockito.`when`(candidateRepository.getCandidateById(expectedCandidateId))
            .thenReturn(flowOf(null)) // Simulate error or empty result

        // Call the method in the ViewModel
        viewModel.fetchingCandidateById(expectedCandidateId)
        assertEquals(Candidate.createDefaultCandidate(),viewModel.candidate.value)

    }


    /**
     * Test the updateCandidate method in the ViewModel
     */
    @Test
    fun test_updateCandidate_updatesCandidate_withValidData(): Unit = runBlocking {
        val candidateToUpdate = Candidate.createDefaultCandidate(
            firstname = "John",
            lastname = "Doe",
            phone = "1234567890",
            email = "john.doe@example.com"
            )

        // Mock the repository to return successful update
        Mockito.`when`(candidateRepository.updateCandidate(candidateToUpdate.toDtoWithId()))
            .thenReturn(1) // Simulate successful update

        // Call the method in the ViewModel
        viewModel.updateCandidate(candidateToUpdate)

        // Verify the repository is called with the expected candidate
        Mockito.verify(candidateRepository).updateCandidate(candidateToUpdate.toDto())
    }







}