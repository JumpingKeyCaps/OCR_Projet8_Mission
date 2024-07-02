package com.ocrmission.vitesse.ui.viewmodel

import com.ocrmission.vitesse.data.repository.CandidateRepository
import com.ocrmission.vitesse.data.repository.CurrencyRepository
import com.ocrmission.vitesse.domain.Candidate
import com.ocrmission.vitesse.domain.NetworkState
import com.ocrmission.vitesse.ui.detailsCandidate.DetailsCandidateViewModel
import com.ocrmission.vitesse.ui.utility.DataInputValidator.Companion.formatDateBirthday
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import java.time.LocalDateTime

/**
 * Unit tests for the DetailsCandidateViewModel
 */
@RunWith(JUnit4::class)
class DetailsCandidateViewModelTest {

    @Mock
    private lateinit var candidateRepository: CandidateRepository
    private lateinit var currencyRepository: CurrencyRepository

    private lateinit var viewModel: DetailsCandidateViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        // Initialize the mock before creating the viewModel
        currencyRepository = mock(CurrencyRepository::class.java)
        viewModel = DetailsCandidateViewModel(candidateRepository, currencyRepository)

    }

    /**
     * Unit test for the fetchingCandidateById method
     */
    @Test
    fun test_fetchingCandidateById_updatesCandidateFlow_withValidData() {
        val candidateId = 0L
        val expectedCandidateDto = Candidate.createDefaultCandidate().toDto()
        val expectedCandidate = Candidate.fromDto(expectedCandidateDto)
        // Mock the repository behavior
        Mockito.`when`(candidateRepository.getCandidateById(candidateId))
            .thenReturn(flowOf(expectedCandidateDto)) // Simulate successful data flow
        // Call the method on the view model
        viewModel.fetchingCandidateById(candidateId)
        // Verify the candidate value is updated (though not the actual emission due to asynchronous nature)
        assertNotNull(viewModel.candidate.value) // Assert value is not null
        assertEquals(expectedCandidate, viewModel.candidate.value) // Assert the values are equal
    }

    /**
     * Unit test for the deleteCandidate method
     */
    @Test
    fun test_deleteCandidate_callsRepository_andUpdatesFavoriteFlow_onSuccessfulDeletion(): Unit = runBlocking {
        val expectedCandidate = Candidate.createDefaultCandidate()
        // Mock the repository behavior
        Mockito.`when`(candidateRepository.deleteCandidate(expectedCandidate.toDtoWithId()))
            .thenReturn(Unit) // Simulate successful deletion
        // Call the method on the view model
        viewModel.deleteCandidate()
        // Verify the repository is called with the expected candidate
        Mockito.verify(candidateRepository).deleteCandidate(expectedCandidate.toDtoWithId())
    }

    /**
     * Unit test for the updateCandidate method
     */
    @Test
    fun test_updateFavoriteState_updatesCandidateAndFlow_onSuccessfulUpdate(): Unit = runBlocking {
        val newFavoriteState = true
        val expectedUpdatedCandidate = Candidate.createDefaultCandidate(isFavorite = newFavoriteState)
        // Mock the repository behavior
        Mockito.`when`(candidateRepository.updateCandidate(expectedUpdatedCandidate.toDtoWithId()))
            .thenReturn(1) // Simulate successful update (return value > 0)
        // Call the method on the view model
        viewModel.updateFavoriteState(newFavoriteState)
        // Verify candidate value is updated with the new favorite state
        val actualCandidate = viewModel.candidate.value
        assertNotNull(actualCandidate)
        // Verify the repository is called with the expected updated candidate
        Mockito.verify(candidateRepository).updateCandidate(expectedUpdatedCandidate.toDtoWithId())
    }


    /**
     * Unit test for the fetchingCurrencyRate method
     */
    @Test
    fun test_fetchingCurrencyRate_fetchesRate_andUpdatesFlows_onSuccessfulFetch(): Unit = runBlocking {
        val expectedRate = 0.0
        // Mock the repository behavior
        Mockito.`when`(currencyRepository.getConversionRateEurGbp())
            .thenReturn(expectedRate) // Simulate successful rate retrieval
        // Call the method on the view model
        viewModel.fetchingCurrencyRate()
        // Verify network state is updated to "no problem"
        assertEquals(NetworkState(), viewModel.networkState.value)
        // Verify currency rate is updated
        assertEquals(expectedRate, viewModel.currencyRate.value)
    }


    /**
     * Unit test for the birthdayDetailsStringBuilder() method with valid date
     */
    @Test
    fun test_birthdayDetailsStringBuilder_returnsFormattedString_withValidDate() {
        val expectedBirthday = LocalDateTime.of(2000, 1, 1, 0, 0)
        val expectedFormattedDate = formatDateBirthday(expectedBirthday)

        // Call the method on the view model
        val actualString = viewModel.birthdayDetailsStringBuilder(expectedBirthday)

        // Verify the returned string matches the expected formatted date
        assertEquals(expectedFormattedDate, actualString)
    }

    /**
     * Unit test for the birthdayDetailsStringBuilder() method with null date
     */
    @Test
    fun test_birthdayDetailsStringBuilder_returnsEmptyString_withNullDate() {
        // Call the method on the view model with null birthDate
        val actualString = viewModel.birthdayDetailsStringBuilder(null)

        // Verify the returned string is empty
        assertEquals(" ", actualString)
    }


    /**
     * Unit test for the birthdayNumberBuilder() method with valid date
     */
    @Test
    fun test_birthdayNumberBuilder_returnsYear_withValidDate() {
        val expectedBirthday = LocalDateTime.of(2000, 1, 1, 0, 0) // Example birth date
        val expectedYear = 24 // Extracted year
        // Call the method on the view model
        val actualYear = viewModel.birthdayNumberBuilder(expectedBirthday)
        // Verify the returned year matches the expected year
        assertEquals(expectedYear, actualYear)
    }

    /**
     * Unit test for the birthdayNumberBuilder() method with null date
     */
    @Test
    fun test_birthdayNumberBuilder_returnsZero_withNullDate() {
        // Call the method on the view model with null birthDate
        val actualYear = viewModel.birthdayNumberBuilder(null)
        // Verify the returned year is zero
        assertEquals(0, actualYear)
    }

    /**
     * Unit test for the Formatted String returned with a valid date
     */
    @Test
    fun test_formatDateBirthday_returnsFormattedString_withValidDate() {
        val expectedBirthday = LocalDateTime.of(2000, 1, 1, 0, 0)
        val expectedFormattedDate = "01/01/2000"
        // Call the method with the expected birth date
        val actualFormattedDate = formatDateBirthday(expectedBirthday)
        // Verify the returned string matches the expected formatted date
        assertEquals(expectedFormattedDate, actualFormattedDate)
    }

    /**
     * Unit test for the Formatted String returned with a null date
     */
    @Test
    fun test_formatDateBirthday_returnsEmptyString_withNullDate() {
        // Call the method with null date
        val actualFormattedDate = formatDateBirthday(null)
        // Verify the returned string is empty
        assertEquals(" ", actualFormattedDate)
    }






}