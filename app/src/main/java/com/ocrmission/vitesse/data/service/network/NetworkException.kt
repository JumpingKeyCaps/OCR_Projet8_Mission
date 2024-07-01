package com.ocrmission.vitesse.data.service.network

/**
 * A Class to Handling all the potential network exceptions.
 *
 *
 *  - ServerErrorException : this is for all server side errors.
 *  - NetworkConnectionException : this is for all network connectivity errors.
 *  - UnknownNetworkException : this is for all other errors.
 */
sealed class NetworkException : Exception(){

    data class ServerErrorException(val errorCode: Int) : NetworkException()

    data class NetworkConnectionException(val isSocketTimeout: Boolean, val isConnectFail: Boolean) : NetworkException()

    data object UnknownNetworkException: NetworkException() {
        private fun readResolve(): Any = UnknownNetworkException
    }
}