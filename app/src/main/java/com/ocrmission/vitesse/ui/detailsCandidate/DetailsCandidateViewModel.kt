package com.ocrmission.vitesse.ui.detailsCandidate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ocrmission.vitesse.data.repository.CandidateRepository
import com.ocrmission.vitesse.data.repository.CurrencyRepository
import com.ocrmission.vitesse.domain.Candidate
import com.ocrmission.vitesse.ui.utils.DataInputValidator
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

    private val _candidate = MutableStateFlow(Candidate(firstname = "", lastname = "", birthday = null, isFavorite = false,note = "", email = "", phone = "",salary = 0))
    val candidate: StateFlow<Candidate> = _candidate.asStateFlow()

    private val _currencyRate = MutableStateFlow<Double>(0.0)
    val currencyRate: StateFlow<Double> = _currencyRate.asStateFlow()


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

    fun getCurrencyRate(fromCurrency: String, toCurrency: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val rate = currencyRepository.getConversionRateFor(fromCurrency, toCurrency)
            _currencyRate.value = rate
        }
    }





//JOBBING METHODS -----------------------------------------------------------


    /**
     * Method to format the birthday details
     * @param birthDate the candidate birthday
     * @param dynamicString the other words with translation support
     * @return the formatted birthday details
     */
    fun birthdayDetailsStringBuilder(birthDate: LocalDateTime?, dynamicString:String): String{
        return DataInputValidator.birthdayDetailsStringBuilder(birthDate, dynamicString)
    }


}