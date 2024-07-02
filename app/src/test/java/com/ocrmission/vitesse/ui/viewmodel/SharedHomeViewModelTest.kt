package com.ocrmission.vitesse.ui.viewmodel

import com.ocrmission.vitesse.data.repository.CandidateRepository
import com.ocrmission.vitesse.domain.Candidate
import com.ocrmission.vitesse.ui.home.SharedHomeViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

/**
 *  Unit tests for the implementation of the HomeViewModel
 */

@RunWith(JUnit4::class)
class SharedHomeViewModelTest {

    @Mock
    private lateinit var candidateRepository: CandidateRepository

    private lateinit var viewModel: SharedHomeViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        viewModel = SharedHomeViewModel(candidateRepository)
    }


    /**
     * Test the initial candidates list state of the viewModel (empty candidates list)
     */
    @Test
    fun test_fetchFilteredCandidates_emitsEmptyList_whenNoCandidatesReturned(): Unit = runBlocking {
        // Mock repository behavior
        Mockito.`when`(candidateRepository.getAllCandidates()).thenReturn(flowOf(emptyList()))
        // Act
        viewModel.fetchFilteredCandidates()
        // viewModel auto trigger the initial fetch at his init block (on test setUp())
        // Assert
        assertEquals( emptyList<Candidate>(), viewModel.candidates.value)
        assertEquals( emptyList<Candidate>(), viewModel.favCandidates.value)
    }



    /**
     * Test the candidates list state of the viewModel (loaded candidates list)
     */
    @Test
    fun test_fetchFilteredCandidates_emitsFilteredList_whenCandidatesReturned(): Unit = runTest {
        // Mock repository behavior with sample candidates
        val expectedCandidatesList = listOf(
            Candidate.createDefaultCandidate(firstname = "John", lastname ="Doe", email ="test1@test.test", phone = "0602020202", salary =  10000, isFavorite = false).toDto(),
        )
         val candidateFlow = flow { emit(expectedCandidatesList) }
        Mockito.`when`(candidateRepository.getAllCandidates()).thenReturn(candidateFlow)

        viewModel.updateFilter("")
        // Act (trigger the fetch only once)
        viewModel.fetchFilteredCandidates() // call it once


    //TODO ------------------------------- NOT FINISHED
        // Assert that the candidates flow emits the expected filtered list
        candidateFlow.collect { actualCandidates ->
            assertEquals(expectedCandidatesList.map { Candidate.fromDto(it) }, actualCandidates.map { Candidate.fromDto(it) })
        }
    }


    /**
     * Test the updateFilter method of the viewModel
     */
    @Test
    fun test_updateFilter_updatesFilterValue() {
        // Arrange
        val initialFilter = viewModel.filter.value // Get initial value (likely empty)
        val newFilter = "jane"
        // Act
        viewModel.updateFilter(newFilter)
        // Assert
        val updatedFilter = viewModel.filter.value
        assertEquals(newFilter, updatedFilter)
        assertNotEquals(initialFilter, updatedFilter)
    }




}