package com.ocrmission.vitesse.data.repository

import com.ocrmission.vitesse.data.service.VitesseApiService
import javax.inject.Inject
/**
 * Repository to the currencies rate data.
 */
class CurrencyRepository @Inject constructor(private val vitesseApiService: VitesseApiService){

    /**
     *  Method to get the currencies rate for  EUR to GBP.
     * @return the currency rate.
     */
    suspend fun getConversionRateEurGbp(): Double {
        return vitesseApiService.getConversionRateEurToGbp()
    }

}