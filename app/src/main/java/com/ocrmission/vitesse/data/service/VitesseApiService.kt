package com.ocrmission.vitesse.data.service

import com.ocrmission.vitesse.data.service.network.interfaces.VitesseNetworkService
import javax.inject.Inject

/**
 * The app API service to communicate with the server.
 */
class VitesseApiService @Inject constructor(private val vitesseNetworkService: VitesseNetworkService) {

    /**
     * Method to get the currency conversion rate between euro and british pound.
     * @return the rate of conversion.
     */
    suspend fun getConversionRateEurToGbp(): Double {
        return vitesseNetworkService.getConversionRateEurTo("gbp") ?: 0.0
    }

}