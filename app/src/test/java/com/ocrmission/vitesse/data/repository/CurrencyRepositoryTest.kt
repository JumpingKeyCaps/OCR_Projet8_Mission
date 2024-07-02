package com.ocrmission.vitesse.data.repository

import com.ocrmission.vitesse.data.service.VitesseApiService
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@RunWith(JUnit4::class)
class CurrencyRepositoryTest {

    @Mock
    private lateinit var vitesseApiService: VitesseApiService

    private lateinit var currencyRepository: CurrencyRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        currencyRepository = CurrencyRepository(vitesseApiService)
    }

    /**
     * Test the behavior of getConversionRateEurGbp method.
     */
    @Test
    fun test_currencyRepository_fetchesEurGbpRate_whenVitesseServiceInvoked() = runTest {
        // Mock vitesseApiService behavior
        val expectedRate = 0.87 // Example rate
        Mockito.`when`(vitesseApiService.getConversionRateEurToGbp()).thenReturn(expectedRate)
        // Act
        val actualRate = currencyRepository.getConversionRateEurGbp()
        // Assert
        assertEquals(expectedRate, actualRate)
    }
}