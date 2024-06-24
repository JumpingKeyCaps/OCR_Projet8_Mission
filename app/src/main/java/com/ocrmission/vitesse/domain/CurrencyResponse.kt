package com.ocrmission.vitesse.domain

/**
 * Data class model of the currency converter response.
 * @param eur the map of all the currency rate for the given currency
 */
data class CurrencyResponse(val eur: Map<String, Double>)