package com.ocrmission.vitesse.ui.addCandidate

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
     * @return True if the text is valid, false otherwise.
     */
    fun validateInput(text: String,typeInput: Int): Boolean {
        //Empty case
        if(text.isEmpty()){
            throw EmptyTextException()
        }

        when(typeInput){
            //firstname  --  Allow only letters and hyphens (no spaces or apostrophes)
            1 -> {
                if (!text.all { it.isLetter() || it == '-' || it == ' ' }) {
                    throw ForbidenCharException()
                }
            }
            //lastname --  Allow letters, spaces, hyphens, and apostrophes
            2 -> {
                if (!text.all { it.isLetter() || it.isWhitespace() || it == '-' || it == '\'' }) {
                    throw ForbidenCharException()
                }
            }
            //mail -- Allow letters, numbers, dots, underscores, and "@" + mail format
            3 -> {
                val atIndex = text.indexOf('@')
                val dotIndex = text.lastIndexOf('.')
                // Basic filtering (letters, numbers, dots, '@', underscore)
                if (!text.all { it.isLetterOrDigit() || it == '.' || it == '@' || it == '_' }) {
                    throw ForbidenCharException()
                }

                // Check for exactly one "@" symbol, and the last dot is after it, and not the last character
                if (text.count { it == '@' } != 1 ||
                    atIndex < 0 ||
                    dotIndex < atIndex ||
                    dotIndex == text.lastIndex) {
                    throw EmailFormatException()
                }

                // Check for at least one dot after "@" and no underscore
                val textAfterAt = text.substring(atIndex + 1)
                if (!textAfterAt.contains('.') || textAfterAt.contains('_')) {
                    throw EmailFormatException()
                }

                // Ensure email doesn't start with ., _, or @
                if (text[0] == '.' || text[0] == '_' || text[0] == '@') {
                    throw EmailFormatException()
                }

                // Forbidden mail Pattern
                if (text.contains("__")
                    || text.contains("..")
                    || text.contains("._")
                    || text.contains("_.")
                    || text.contains("_@")
                    || text.contains("@_")
                    || text.contains(".@")
                    || text.contains("@.")
                    ) {
                    throw ForbidenCharException()
                }

            }
            //phone  -- TODO BETTERVERSION (size)
            4 -> {
                // Check if the text contains only digits
                if (!text.all { it.isDigit() }) {
                    throw ForbidenCharException()
                }

                // Check for valid phone number length (minimum 7, maximum 15 digits)
                if (text.length < 7) {
                    throw PhoneLengthException()
                }

            }
            //salary
            5 -> {
                if (!text.all { it.isDigit() }) {
                    throw ForbidenCharException()
                }
            }
            //notes
            6 -> {
                if (!text.all { it.isLetterOrDigit() || it == '.' || it == ' ' || it == '\'' }) {
                    throw ForbidenCharException()
                }
            }
            else -> { return false }
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