package com.ocrmission.vitesse.data.repository

import com.ocrmission.vitesse.data.service.VitesseApiService
import javax.inject.Inject
/**
 * Repository to the currencies rate data.
 */
class CurrencyRepository @Inject constructor(private val vitesseApiService: VitesseApiService){

    /**
     *  method to get the currencies rate.
     * @param baseCurrency base currency.
     * @param targetCurrency target currency.
     * @return the currency rate.
     */
    suspend fun getConversionRateFor(baseCurrency: String, targetCurrency: String): Double {
        return vitesseApiService.getConversionRateFor(baseCurrency, targetCurrency)
    }

}