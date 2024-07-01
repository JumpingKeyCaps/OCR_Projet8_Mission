package com.ocrmission.vitesse.data.service.network

import com.ocrmission.vitesse.data.service.network.interfaces.RetrofitService
import com.ocrmission.vitesse.data.service.network.interfaces.VitesseNetworkService
import java.net.ConnectException
import java.net.SocketTimeoutException
import javax.inject.Inject

/**
 * Vitesse Network Service implementation class.
 *  - Redefine all the interface network methods to be use with the instance of retrofit.
 */

class VitesseNetworkServiceImpl @Inject constructor(private val retrofitService: RetrofitService):
    VitesseNetworkService {


    /**
     * Fetches conversion rates for EURO to the given target currency.
     * @param targetCurrency The target currency for which conversion rates are fetched.
     * @return the conversion rates for EUR to the given target currency.
     * @throws Exception If an error occurs during the network request or data parsing.
     */
    override suspend fun getConversionRateEurTo(targetCurrency: String): Double? {
        try {
            //Get the conversion rate
            val response = retrofitService.getConversionRate("eur")
            //check if the response is successful
            if(response.isSuccessful){
                //extract the eur-gbp conversion rate from the response body
                return response.body()?.getEuroRateFor(targetCurrency)
            }else{
                //reponse is not successful - throw a custom exception
                throw NetworkException.ServerErrorException(response.code())
            }
        } catch (e: Exception) {
            //handle network/others exceptions
            throw customNetworkException(e)
        }
    }


    /**
     * Methode to throw a custom NetworkException based on the type of exceptions.
     *
     * @param e the raised exception.
     * @return  Throwable custom network exception.
     */
    private fun customNetworkException(e:Exception): Throwable{
        return when (e) {
            is SocketTimeoutException -> NetworkException.NetworkConnectionException(isSocketTimeout = true,isConnectFail = false)
            is ConnectException -> NetworkException.NetworkConnectionException(isSocketTimeout = false,isConnectFail = true)
            else -> NetworkException.UnknownNetworkException
        }
    }


}