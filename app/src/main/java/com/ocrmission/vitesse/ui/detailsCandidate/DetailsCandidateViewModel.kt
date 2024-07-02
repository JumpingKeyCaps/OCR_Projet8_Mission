package com.ocrmission.vitesse.ui.detailsCandidate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ocrmission.vitesse.R
import com.ocrmission.vitesse.data.repository.CandidateRepository
import com.ocrmission.vitesse.data.repository.CurrencyRepository
import com.ocrmission.vitesse.data.service.network.NetworkException
import com.ocrmission.vitesse.domain.Candidate
import com.ocrmission.vitesse.domain.Candidate.Companion.createDefaultCandidate
import com.ocrmission.vitesse.domain.NetworkState
import com.ocrmission.vitesse.ui.utility.DataInputValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * View Model for DetailsCandidate fragment screen
 */
@HiltViewModel
class DetailsCandidateViewModel @Inject constructor(
    private val candidateRepository: CandidateRepository,
    private val currencyRepository: CurrencyRepository
) : ViewModel() {

    private val _candidate = MutableStateFlow(createDefaultCandidate())
    val candidate: StateFlow<Candidate> = _candidate.asStateFlow()

    private val _currencyRate = MutableStateFlow<Double>(0.0)
    val currencyRate: StateFlow<Double> = _currencyRate.asStateFlow()

    private val _networkState = MutableStateFlow(NetworkState())
    val networkState: StateFlow<NetworkState> = _networkState.asStateFlow()

    /**
     * Method to fetch candidate by his id
     * @param candidateId id of the candidate
     */
    fun fetchingCandidateById(candidateId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            candidateRepository.getCandidateById(candidateId)
                .collect{ candidateDto ->
                    if(candidateDto != null ){
                        _candidate.value = Candidate.fromDto(candidateDto)
                    }
                  }
        }
    }

    /**
     * Method to delete candidate from database
     */
    fun deleteCandidate() {
        viewModelScope.launch(Dispatchers.IO) {
            candidateRepository.deleteCandidate(_candidate.value.toDtoWithId())
        }
    }

    /**
     * Method to update candidate favorite state
     * @param newFavoriteState the new favorite state
     * @return 1 if the update is successful, 0 if the update is failed
     */
    fun updateFavoriteState(newFavoriteState: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            candidate.value.isFavorite = newFavoriteState
            val updateResult = candidateRepository.updateCandidate(candidate.value.toDtoWithId())
            if (updateResult > 0) {
                //update good, so update the candidate flow
                _candidate.value.isFavorite = newFavoriteState
            }
        }
    }


//CURRENCY CONVERTER METHODS -----------------------------------------------------------

    /**
     * Method to fetch the currency rate from the API
     */
    fun fetchingCurrencyRate() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                //Update currency rate
                val rate = currencyRepository.getConversionRateEurGbp()
                _currencyRate.value = rate
                //Update network state to "no problem"
                _networkState.value = NetworkState()
            }catch (e: Exception){
                // Handle network errors, server errors, etc.
                // (Expose the NetworkState flow with the error msg to the fragment)
                when (e) {
                    is NetworkException.ServerErrorException ->{ _networkState.value = NetworkState(true, R.string.server_error) }
                    is NetworkException.NetworkConnectionException  ->{
                        if(e.isSocketTimeout){_networkState.value = NetworkState(true,R.string.timeout_error)}
                        else if(e.isConnectFail){_networkState.value = NetworkState(true,R.string.connection_fail_error)}
                        else{ _networkState.value = NetworkState(true,R.string.network_generic_error)}
                    }
                    is NetworkException.UnknownNetworkException  ->{ _networkState.value = NetworkState(true,R.string.network_fail_error) }
                    else -> { _networkState.value = NetworkState(true,R.string.network_generic_error)}
                }
            }
        }
    }

    /**
     * Method to get the candidate salary in pound
     * @return the candidate salary in pound
     */
    fun getSalaryInPound(): Double {
        return candidate.value.salary * currencyRate.value
    }


//JOBBING METHODS -----------------------------------------------------------


    /**
     * Method to format the birthday details
     * @param birthDate the candidate birthday
     * @return the formatted birthday details
     */
    fun birthdayDetailsStringBuilder(birthDate: LocalDateTime?): String{
        return DataInputValidator.birthdayDetailsStringBuilder(birthDate)
    }

    /**
     * Method to calculate the birthday date into number of lived years
     * @param birthDate the candidate birthday
     * @return the birthday in number of lived years
     */
    fun birthdayNumberBuilder(birthDate: LocalDateTime?): Int{
        return DataInputValidator.birthdayNumberBuilder(birthDate)
    }

}