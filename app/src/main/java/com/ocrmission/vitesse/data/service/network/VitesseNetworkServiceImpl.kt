package com.ocrmission.vitesse.data.service.network

import android.util.Log
import com.ocrmission.vitesse.data.service.network.interfaces.RetrofitService
import com.ocrmission.vitesse.data.service.network.interfaces.VitesseNetworkService
import javax.inject.Inject

/**
 * Vitesse Network Service implementation class.
 *
 * Redefine all the interface network methods to be use with the instance of retrofit.
 */

class VitesseNetworkServiceImpl @Inject constructor(private val retrofitService: RetrofitService):
    VitesseNetworkService {


    /**
     * Fetches conversion rates for all currencies relative to the provided currency.
     *
     * @param currency The currency code to convert from (e.g., "USD").
     * @return A map containing conversion rates for all currencies relative to the provided currency.
     * The key is the currency code and the value is the conversion rate.
     * @throws Exception If an error occurs during the network request or data parsing.
     */
    override suspend fun getConversionRate(currency: String): Map<String, Double> {
        try {

            val response = retrofitService.getConversionRate(currency)
            return response.eur  // Access the rate map from the response


        } catch (e: Exception) {
            Log.d("ConvertionRate", "getConversionRate: ${e.toString()}")
            throw Exception("Error $e")
        }
    }


}