package com.ocrmission.vitesse.data.service.network.interfaces

/**
 * Vitesse network service interface with all the network methods.
 */
interface VitesseNetworkService {
    /**
     * Method to request the currency conversion rate on the network.
     * @param currency The currency source.
     * @return A map of currencies and their conversion rates for the source currency.
     */
    suspend fun getConversionRate(currency: String): Map<String, Double>
}