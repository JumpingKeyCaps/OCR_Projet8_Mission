package com.ocrmission.vitesse.ui.addCandidate

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ocrmission.vitesse.data.repository.CandidateRepository
import com.ocrmission.vitesse.domain.Candidate
import com.ocrmission.vitesse.ui.addCandidate.exceptions.EmailFormatException
import com.ocrmission.vitesse.ui.addCandidate.exceptions.EmptyTextException
import com.ocrmission.vitesse.ui.addCandidate.exceptions.ForbidenCharException
import com.ocrmission.vitesse.ui.addCandidate.exceptions.MissingEmailException
import com.ocrmission.vitesse.ui.addCandidate.exceptions.MissingFirstNameException
import com.ocrmission.vitesse.ui.addCandidate.exceptions.MissingLastNameException
import com.ocrmission.vitesse.ui.addCandidate.exceptions.MissingPhoneException
import com.ocrmission.vitesse.ui.addCandidate.exceptions.MissingBirthException
import com.ocrmission.vitesse.ui.addCandidate.exceptions.PhoneLengthException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

/**
 * The ViewModel for the AddCandidate fragment.
 */
@HiltViewModel
class AddCandidateViewModel  @Inject constructor(
    private val candidateRepository: CandidateRepository
) : ViewModel() {


    /**
     * Method to validate the text inputs of the user.
     * @param text The text to validate.
     * @param typeInput The type of filter to apply ont the text to be valid.
     *  (1 = firstname 2 = lastname 3 = mail 4 = phone 5 = salary 6 = notes)
     * @return True if the text is valid, false otherwise.
     */
    fun validateInput(text: String, typeInput: Int): Boolean {
        // Empty case
        if (text.isEmpty()) throw EmptyTextException()

        when (typeInput) {
            // First name
            1 -> {
                val namePattern = Regex("^[a-zA-Z -]+$") // Allow only letters and hyphens
                if (!namePattern.matches(text)) throw ForbidenCharException()
            }
            // Last name
            2 -> {
                val namePattern = Regex("^[a-zA-Z '-]+$") // Allow letters, hyphens, and spaces
                if (!namePattern.matches(text)) throw ForbidenCharException()
            }
            // Email
            3 -> {
                if (!Patterns.EMAIL_ADDRESS.matcher(text).matches()) throw EmailFormatException()
            }
            // Phone (basic 10-digit format)
            4 -> {
                val phonePattern = Regex("^[0-9]{10}$") // Allow only 10 digits
                if (!phonePattern.matches(text)) throw PhoneLengthException()
            }
            // Salary
            5 -> {
                val salaryPattern = Regex("^[0-9]+$") // Allow only digits
                if (!salaryPattern.matches(text)) throw ForbidenCharException()
            }
            // Notes
            6 -> {
                val notePattern = Regex("^[a-zA-Z0-9 .'-]+$") // Allow letters, digits, periods, spaces, and apostrophes
                if (!notePattern.matches(text)) throw ForbidenCharException()
            }
            else -> return false
        }
        return true
    }



    /**
     * Method to add a candidate in secure way to the database.
     * @param candidate The candidate to add.
     */
    fun addCandidate(candidate: Candidate) {
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
            //all is ok to add a new candidate
            viewModelScope.launch(Dispatchers.IO) {
                candidateRepository.addCandidate(candidate.toDto())
            }
        }
    }


    /**
     * Method to convert a date string to a LocalDateTime object.
     * @param dateString The date string to convert.
     * @return The converted LocalDateTime object, or null if the conversion fails (bad format,empty,etc...).
     */
    fun birthdayDateConverter(dateString: String): LocalDateTime? {
        // Parse the date string using SimpleDateFormat
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

         try {
            val parsedDate = formatter.parse(dateString)
             if (parsedDate != null) {
                val calendar = Calendar.getInstance()
                val currentDate = calendar.timeInMillis

                // Check if parsed date is not in the future
                 return if (parsedDate.time <= currentDate) {
                     // Valid date, convert to LocalDateTime
                     LocalDateTime.ofInstant(parsedDate.toInstant(), ZoneId.systemDefault())
                 } else {
                     // Future date, then return null to trigger missingBirthException in later addCandidate() call
                     null
                 }
            } else {
                // Handle invalid date format or the default "dd/MM/yyyy set in the edittext"
                 return null
            }
        }catch (e: ParseException){ // parsing failed, it's not a valid date
            return null
        }


        // Convert the parsed Date to LocalDateTime


    }

}