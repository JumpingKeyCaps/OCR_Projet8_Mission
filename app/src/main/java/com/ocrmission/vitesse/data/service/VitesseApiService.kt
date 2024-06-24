package com.ocrmission.vitesse.data.service

import com.ocrmission.vitesse.data.service.network.interfaces.VitesseNetworkService
import javax.inject.Inject

/**
 * The app API service to communicate with the server.
 */
class VitesseApiService @Inject constructor(private val vitesseNetworkService: VitesseNetworkService) {

    /**
     * Method to get the currency conversion rate
     * @param currencySource the base currency
     * @param currencyDestination the target currency rate
     * @return the rate of conversion.
     */
    suspend fun getConversionRateFor(currencySource: String, currencyDestination: String): Double {
        val conversionRates =  vitesseNetworkService.getConversionRate(currencySource)
        val rate = conversionRates[currencyDestination] ?: 0.0 // Handle potential missing value
        return rate
    }

}