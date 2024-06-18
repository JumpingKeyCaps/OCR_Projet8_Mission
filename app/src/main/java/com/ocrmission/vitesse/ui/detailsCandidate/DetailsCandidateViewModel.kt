package com.ocrmission.vitesse.ui.detailsCandidate

import android.util.Log
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ocrmission.vitesse.R
import com.ocrmission.vitesse.data.repository.CandidateRepository
import com.ocrmission.vitesse.domain.Candidate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * View Model for DetailsCandidate fragment screen
 */
@HiltViewModel
class DetailsCandidateViewModel @Inject constructor(
    private val candidateRepository: CandidateRepository
) : ViewModel() {

    private val _candidate = MutableStateFlow<Candidate?>(null)
    val candidate: StateFlow<Candidate?> = _candidate.asStateFlow()


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
     * @param candidate the candidate object to delete
     */
    fun deleteCandidate(candidate: Candidate?) {
        if (candidate != null) {
            viewModelScope.launch(Dispatchers.IO) {
                candidateRepository.deleteCandidate(candidate.toDto())
            }
        }
    }

    /**
     * Method to delete candidate by Id from database
     * @param candidateId the candidate id to delete
     */
    fun deleteCandidateById(candidateId: Long?) {
        if (candidateId != null) {
            viewModelScope.launch(Dispatchers.IO) {
                candidateRepository.deleteCandidateById(candidateId)
            }
        }
    }


    /**
     * Method to update candidate favorite state
     * @param candidate  the candidate object to update
     * @param newFavoriteState the new favorite state
     * @return 1 if the update is successful, 0 if the update is failed
     */
    fun updateFavoriteState(candidate: Candidate, newFavoriteState: Boolean) {

        Log.d("favorites", "updateFavoriteState: call db ...")

        viewModelScope.launch(Dispatchers.IO) {
            candidate.isFavorite = newFavoriteState

         //   val result = candidateRepository.updateCandidate(candidate.toDto())

            val updateResult  = candidateRepository.updateFavoriteState(candidate.id, newFavoriteState)
            if (updateResult > 0) {
                //update good, so update the candidate flow
                _candidate.value?.isFavorite = newFavoriteState
            }
        }
    }


    /**
     * Method to convert birthday date in years
     * @param birthDate birthday date in LocalDateTime format
     *@return the age in years
     */
    private fun calculateAgeInYears(birthDate: LocalDateTime?): Int {
        if (birthDate == null) {
            return 0
        }else{
            val today = LocalDate.now() // Get current date
            val birthDateAsDate = birthDate.toLocalDate() // Extract date from LocalDateTime
            val period = Period.between(birthDateAsDate, today) // calc the period between the two dates
            return period.years
        }
    }

    /**
     * Method to format birthday date in dd/MM/yyyy format string
     * @param date birthday date in LocalDateTime format
     * @return the formatted birthday date string
     */
    private fun formatDateBirthday(date: LocalDateTime?): String {
        if (date == null) {
            return " "
        }else{
            val formattedBirthDate = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            return formattedBirthDate
        }
    }

    /**
     * Method to format the birthday details
     * @param birthDate the candidate birthday
     * @param dynamicString the other words with translation support
     * @return the formatted birthday details
     */
    fun birthdayDetailsStringBuilder(birthDate: LocalDateTime?, dynamicString:String): String{
        val formattedBirthDate = formatDateBirthday(birthDate)
        val age = calculateAgeInYears(birthDate)
        return "$formattedBirthDate  ($age ${dynamicString})"
    }



}