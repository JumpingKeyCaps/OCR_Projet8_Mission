package com.ocrmission.vitesse.ui.editCandidate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ocrmission.vitesse.data.repository.CandidateRepository
import com.ocrmission.vitesse.domain.Candidate
import com.ocrmission.vitesse.domain.Candidate.Companion.createDefaultCandidate
import com.ocrmission.vitesse.ui.utility.DataInputValidator
import com.ocrmission.vitesse.ui.addCandidate.exceptions.MissingBirthException
import com.ocrmission.vitesse.ui.addCandidate.exceptions.MissingEmailException
import com.ocrmission.vitesse.ui.addCandidate.exceptions.MissingFirstNameException
import com.ocrmission.vitesse.ui.addCandidate.exceptions.MissingLastNameException
import com.ocrmission.vitesse.ui.addCandidate.exceptions.MissingPhoneException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * ViewModel for the fragment to edit a candidate.
 */
@HiltViewModel
class EditCandidateViewModel  @Inject constructor(
    private val candidateRepository: CandidateRepository
) : ViewModel() {

    private val _candidate = MutableStateFlow(createDefaultCandidate())
    val candidate: StateFlow<Candidate> = _candidate.asStateFlow()


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
     * Method to update a candidate.
     * @param candidate The candidate to update.
     */
    fun updateCandidate(candidate: Candidate) {

        if(candidate.firstname.isEmpty()){
            throw MissingFirstNameException()
        }else if(candidate.lastname.isEmpty()){
            throw MissingLastNameException()
        }else if(candidate.phone.isEmpty()){
            throw MissingPhoneException()
        }else if(candidate.email.isEmpty()){
            throw MissingEmailException()
        }else if(candidate.birthday == null){
            throw MissingBirthException()
        }else{
            viewModelScope.launch(Dispatchers.IO) {

                val resultUpdate = candidateRepository.updateCandidate(candidate.toDtoWithId())
                if(resultUpdate != 1){
                    throw Exception("Failed to update candidate")
                }
            }
        }


    }





    /**
     * Method to validate the text inputs of the user.
     * @param text The text to validate.
     * @param typeInput The type of filter to apply ont the text to be valid.
     *  (1 = firstname 2 = lastname 3 = mail 4 = phone 5 = salary 6 = notes)
     * @return True if the text is valid, false otherwise.
     */
    fun validateInput(text: String, typeInput: Int): Boolean {
        return DataInputValidator.validateInput(text, typeInput)
    }




    /**
     * Method to convert a date string to a LocalDateTime object.
     * @param dateString The date string to convert.
     * @return The converted LocalDateTime object, or null if the conversion fails (bad format,empty,etc...).
     */
    fun birthdayDateConverter(dateString: String): LocalDateTime? {
        return DataInputValidator.birthdayDateConverter(dateString)
    }


    /**
     * Method to format birthday date in dd/MM/yyyy format string
     * @param date birthday date in LocalDateTime format
     * @return the formatted birthday date string
     */
    fun formatDateBirthday(date: LocalDateTime?): String {
      return DataInputValidator.formatDateBirthday(date)
    }




}