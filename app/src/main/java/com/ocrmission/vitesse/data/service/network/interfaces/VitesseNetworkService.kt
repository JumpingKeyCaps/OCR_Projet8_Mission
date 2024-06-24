package com.ocrmission.vitesse.data.service.network.interfaces

/**
 * Vitesse network service interface with all the network methods.
 */
interface VitesseNetworkService {
    /**
     * Method to request the currency conversion rate on the network.
     */
    suspend fun getConversionRate(currency: String): Map<String, Double>
}