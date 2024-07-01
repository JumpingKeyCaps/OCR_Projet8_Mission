package com.ocrmission.vitesse.domain


/**
 *  Network state class - define the state of the network
 *  @property errorState - true if there is an network error/ false if not
 *  @property errorMessage - error message string reference
 */
data class NetworkState( val errorState: Boolean = false, val errorMessage: Int? = null)
