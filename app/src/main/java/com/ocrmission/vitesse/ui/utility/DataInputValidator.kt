package com.ocrmission.vitesse.ui.utility

import android.util.Patterns
import com.ocrmission.vitesse.ui.addCandidate.exceptions.EmailFormatException
import com.ocrmission.vitesse.ui.addCandidate.exceptions.EmptyTextException
import com.ocrmission.vitesse.ui.addCandidate.exceptions.ForbidenCharException
import com.ocrmission.vitesse.ui.addCandidate.exceptions.PhoneLengthException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

/**
 *  Class Used to validate all data input fields of the user.
 */
class DataInputValidator {

    companion object{

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
                    val phonePattern = Regex("^[0-9]{10}$") // Allow min 10 digits number
                    if (!phonePattern.matches(text)) throw PhoneLengthException()
                }
                // Salary
                5 -> {
                    val salaryPattern = Regex("^[0-9]+$") // Allow only digits
                    if (!salaryPattern.matches(text)) throw ForbidenCharException()
                }
                // Notes
                6 -> {
                    // no filter needed, all type of characters is accepted for notes
                }
                else -> return false
            }
            return true
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
        }


        /**
         * Method to format the birthday details
         * @param birthDate the candidate birthday
         * @return the formatted birthday details
         */
        fun birthdayDetailsStringBuilder(birthDate: LocalDateTime?): String{
            val formattedBirthDate = formatDateBirthday(birthDate)
            return formattedBirthDate
        }

        /**
         * Method to calculate the birthday number of years
         * @param birthDate the candidate birthday
         * @return the birthday number of years
         */
        fun birthdayNumberBuilder(birthDate: LocalDateTime?): Int{
            val age = calculateAgeInYears(birthDate)
            return age
        }


        /**
         * Method to format birthday date in dd/MM/yyyy format string
         * @param date birthday date in LocalDateTime format
         * @return the formatted birthday date string
         */
        fun formatDateBirthday(date: LocalDateTime?): String {
            if (date == null) {
                return " "
            }else{
                val formattedBirthDate = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                return formattedBirthDate
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

    }

}