package com.ocrmission.vitesse.data.service.network.interfaces

import com.ocrmission.vitesse.domain.CurrencyResponse
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * The Retrofit service interface to communicate with the currency converter API server.
 */
interface RetrofitService {

    /**
     * Method to request the currency conversion rate to the server for a given currency.
     * @param from the currency code source reference.
     * @return a CurrencyResponse object.
     */
    @GET("currencies/{from}.json")
    suspend fun getConversionRate(@Path("from") from: String): CurrencyResponse
}