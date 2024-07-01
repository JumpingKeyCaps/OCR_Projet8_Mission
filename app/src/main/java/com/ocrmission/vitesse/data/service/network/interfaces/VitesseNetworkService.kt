package com.ocrmission.vitesse.data.service.network.interfaces

/**
 * Vitesse network service interface with all the network methods.
 */
interface VitesseNetworkService {
    /**
     * Method to request the EURO currency conversion rate on the network.
     * @param targetCurrency the target currency.
     * @return the currency rate between EUR to the target currency.
     */
    suspend fun getConversionRateEurTo(targetCurrency: String): Double?
}