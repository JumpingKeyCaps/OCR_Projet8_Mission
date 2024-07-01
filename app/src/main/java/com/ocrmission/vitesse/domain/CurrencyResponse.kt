package com.ocrmission.vitesse.domain


/**
 * Data class model of the currency converter response.
 * @param eur the map of all the currency rate for the given currency
 */
data class CurrencyResponse(val eur: Map<String, Double>){
    /**
     * Get the EURO currency rate for the given currency code.
     * @param currencyCode the target currency code
     * @return the EURO currency rate for the given currency code
     */
    fun getEuroRateFor(currencyCode: String): Double? {
        return eur[currencyCode]
    }

}