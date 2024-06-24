package com.ocrmission.vitesse.data.repository

import com.ocrmission.vitesse.data.service.VitesseApiService
import javax.inject.Inject

/**
 * Repository to retrieve currencies rate.
 */
class CurrencyRepository @Inject constructor(private val vitesseApiService: VitesseApiService){

    /**
     * Get currencies rate.
     * @param baseCurrency base currency.
     * @param targetCurrency target currency.
     * @return the currency rate.
     */
    suspend fun getConversionRateFor(baseCurrency: String, targetCurrency: String): Double {
        return vitesseApiService.getConversionRateFor(baseCurrency, targetCurrency)
    }



}