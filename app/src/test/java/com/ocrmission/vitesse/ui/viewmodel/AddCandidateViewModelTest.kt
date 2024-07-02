package com.ocrmission.vitesse.ui.viewmodel

import com.ocrmission.vitesse.data.repository.CandidateRepository
import com.ocrmission.vitesse.domain.Candidate
import com.ocrmission.vitesse.ui.addCandidate.AddCandidateViewModel
import com.ocrmission.vitesse.ui.addCandidate.exceptions.EmptyTextException
import com.ocrmission.vitesse.ui.addCandidate.exceptions.ForbidenCharException
import com.ocrmission.vitesse.ui.addCandidate.exceptions.MissingBirthException
import com.ocrmission.vitesse.ui.addCandidate.exceptions.MissingEmailException
import com.ocrmission.vitesse.ui.addCandidate.exceptions.MissingFirstNameException
import com.ocrmission.vitesse.ui.addCandidate.exceptions.MissingLastNameException
import com.ocrmission.vitesse.ui.addCandidate.exceptions.MissingPhoneException
import com.ocrmission.vitesse.ui.addCandidate.exceptions.PhoneLengthException
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.time.LocalDate

/**
 *  Unit tests for the implementation of the AddCandidateViewModel.
 */

@RunWith(JUnit4::class)
class AddCandidateViewModelTest {

    @Mock
    private lateinit var candidateRepository: CandidateRepository

    private lateinit var viewModel: AddCandidateViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        viewModel = AddCandidateViewModel(candidateRepository)
    }


    //AddCandidate method
    /**
     * Tests the addCandidate method of the AddCandidateViewModel for an empty first name.
     */
    @Test
    fun test_addCandidate_throwsMissingFirstNameException_whenFirstNameIsEmpty() {
        val candidate = Candidate.createDefaultCandidate()
        candidate.firstname = ""// set empty name to trigger
        try {
            viewModel.addCandidate(candidate)
            fail("Expected Exception")
        } catch (e: Exception) {
            if(e is MissingFirstNameException ){
                // Expected exception, test passes !
            }else{
                fail("Expected MissingFirstNameException")
            }
        }
    }

    /**
     * Tests the addCandidate method of the AddCandidateViewModel for an empty last name.
     */
    @Test
    fun test_addCandidate_throwsMissingLastNameException_whenLastNameIsEmpty() {
        val candidate = Candidate.createDefaultCandidate(firstname = "jane") // set the first name to not trigger firstname exception
        candidate.lastname = ""// set empty last name to trigger
        try {
            viewModel.addCandidate(candidate)
            fail("Expected Exception")
        } catch (e: Exception) {
            if(e is MissingLastNameException){
                // Expected exception, test passes !
            }else{
                fail("Expected MissingLastNameException")
            }
        }
    }

    /**
     * Tests the addCandidate method of the AddCandidateViewModel for a missing phone number.
     */
    @Test
    fun test_addCandidate_throwsMissingPhoneException_whenPhoneIsEmpty() {
        val candidate = Candidate.createDefaultCandidate(
            firstname = "jane",
            lastname = "doe") // set the first and last name to not trigger firstname/lastname exception
        candidate.phone = ""// set empty phone number to trigger the exception
        try {
            viewModel.addCandidate(candidate)
            fail("Expected Exception")
        } catch (e: Exception) {
            if(e is MissingPhoneException){
                // Expected exception, test passes !
            }else{
                fail("Expected MissingPhoneException")
            }
        }
    }

    /**
     * Tests the addCandidate method of the AddCandidateViewModel for a missing email address.
     */
    @Test
    fun test_addCandidate_throwsMissingEmailException_whenEmailIsEmpty() {
        val candidate = Candidate.createDefaultCandidate(
            firstname = "jane",
            lastname = "doe",
            phone = "1234567890") // set the first /last name/phone to not trigger firstname/lastname/phone exception
        candidate.email = ""// set empty email address to trigger the exception
        try {
            viewModel.addCandidate(candidate)
            fail("Expected Exception")
        } catch (e: Exception) {
            if(e is MissingEmailException){
                // Expected exception, test passes !
            }else{
                fail("Expected MissingEmailException")
            }
        }
    }

    /**
     * Tests the addCandidate method of the AddCandidateViewModel for a missing phone number.
     */
    @Test
    fun test_addCandidate_throwsMissingBirthdayException_whenBirthdateIsEmpty() {
        val candidate = Candidate.createDefaultCandidate(
            firstname = "jane",
            lastname = "doe",
            phone = "1234567890",
            email = "jane@doe.com") // set the first/last name,phone,mail to not trigger firstname/lastname/phone/mail exception
        candidate.birthday = null // set null birthday to trigger the exception
        try {
            viewModel.addCandidate(candidate)
            fail("Expected Exception")
        } catch (e: Exception) {
            if(e is MissingBirthException){
                // Expected exception, test passes !
            }else{
                fail("Expected MissingBirthException")
            }
        }
    }


    //first name input validation
    /**
     * Tests the validateInput method of the AddCandidateViewModel for a valid first name.
     */
    @Test
    fun test_validateInput_returnsTrue_forValidFirstName() {
        val text = "John"
        val type = 1 // First Name
        val result = viewModel.validateInput(text, type)
        assertTrue(result)
    }

    /**
     * Tests the validateInput method of the AddCandidateViewModel for an empty first name.
     */
    @Test
    fun test_validateInput_throwsEmptyTextException_forEmptyFirstName() {
        val text = ""
        val type = 1 // First Name
        try {
            viewModel.validateInput(text, type)
            fail("Expected Exception")
        }catch (e: Exception){
            if(e is EmptyTextException){
                // Expected exception, test passes !
            }else{
                fail("Expected EmptyTextException")
            }
        }
    }

    /**
     * Tests the validateInput method of the AddCandidateViewModel for an invalid first name.
     */
    @Test
    fun test_validateInput_throwsForbiddenCharException_forInvalidFirstName() {
        val text = "Joh455h" // number not allowed
        val type = 1 // First Name
        try {
            viewModel.validateInput(text, type)
            fail("Expected Exception")
        }catch (e: Exception){
            if(e is ForbidenCharException){
                // Expected exception, test passes !
            }else{
                fail("Expected ForbiddenCharException")
            }
        }
    }


    //last name input validation
    /**
     * Tests the validateInput method of the AddCandidateViewModel for a valid last name.
     */
    @Test
    fun test_validateInput_returnsTrue_forValidLastName() {
        val text = "Doe"
        val type = 2 // Last Name
        val result = viewModel.validateInput(text, type)
        assertTrue(result)
    }

    /**
     * Tests the validateInput method of the AddCandidateViewModel for an empty last name.
     */
    @Test
    fun test_validateInput_throwsEmptyTextException_forEmptyLastName() {
        val text = ""
        val type = 2 // Last Name
        try {
            viewModel.validateInput(text, type)
            fail("Expected Exception")
        }catch (e: Exception){
            if(e is EmptyTextException){
                // Expected exception, test passes !
            }else{
                fail("Expected EmptyTextException")
            }
        }
    }

    /**
     * Tests the validateInput method of the AddCandidateViewModel for an invalid last name.
     */
    @Test
    fun test_validateInput_throwsForbiddenCharException_forInvalidLastName() {
        val text = "Do3" // number not allowed
        val type = 2 // Last Name
        try {
            viewModel.validateInput(text, type)
            fail("Expected Exception")
        }catch (e: Exception){
            if(e is ForbidenCharException){
                // Expected exception, test passes !
            }else{
                fail("Expected ForbiddenCharException")
            }
        }
    }


    //Email input validation

    /**
     * Tests the validateInput method of the AddCandidateViewModel for an empty e-mail address.
     */
    @Test
    fun test_validateInput_throwsEmptyTextException_forEmptyEmailAddress() {
        val text = ""
        val type = 3 // email
        try {
            viewModel.validateInput(text, type)
            fail("Expected Exception")
        }catch (e: Exception){
            if(e is EmptyTextException){
                // Expected exception, test passes !
            }else{
                fail("Expected EmptyTextException")
            }
        }
    }

    /**
     * Tests the validateInput method of the AddCandidateViewModel for an invalid e-mail address.
     */
    @Test
    fun test_validateInput_throwsForbiddenCharException_forInvalidEmailAddress() {
        val text = "doejane@.doe.fr" // bad email format
        val type = 3 // email
        try {
            viewModel.validateInput(text, type)
            fail("Expected Exception")
        }catch (e: Exception){
            // Expected exception, test passes !
        }
    }


    //Phone input validation
    /**
     * Tests the validateInput method of the AddCandidateViewModel for a valid phone number.
     */
    @Test
    fun test_validateInput_returnsTrue_forValidPhoneNumber() {
        val text = "0664543445"
        val type = 4 // phone
        val result = viewModel.validateInput(text, type)
        assertTrue(result)
    }

    /**
     * Tests the validateInput method of the AddCandidateViewModel for an empty phone number.
     */
    @Test
    fun test_validateInput_throwsEmptyTextException_forEmptyPhoneNumber() {
        val text = ""
        val type = 4 // phone
        try {
            viewModel.validateInput(text, type)
            fail("Expected Exception")
        }catch (e: Exception){
            if(e is EmptyTextException){
                // Expected exception, test passes !
            }else{
                fail("Expected EmptyTextException")
            }
        }
    }

    /**
     * Tests the validateInput method of the AddCandidateViewModel for an invalid phone number.
     */
    @Test
    fun test_validateInput_throwsForbiddenCharException_forInvalidPhoneNumber() {
        val text = "000" // too short
        val type = 4 // phone
        try {
            viewModel.validateInput(text, type)
            fail("Expected Exception")
        }catch (e: Exception){
            if(e is PhoneLengthException){
                // Expected exception, test passes !
            }else{
                fail("Expected PhoneLengthException")
            }
        }
    }


    //Salary input validation
    /**
     * Tests the validateInput method of the AddCandidateViewModel for a valid salary.
     */
    @Test
    fun test_validateInput_returnsTrue_forValidSalary() {
        val text = "15000"
        val type = 5 // salary
        val result = viewModel.validateInput(text, type)
        assertTrue(result)
    }

    /**
     * Tests the validateInput method of the AddCandidateViewModel for an invalid phone number.
     */
    @Test
    fun test_validateInput_throwsForbiddenCharException_forInvalidSalary() {
        val text = "9ft45" // bad format
        val type = 5 // salary
        try {
            viewModel.validateInput(text, type)
            fail("Expected Exception")
        }catch (e: Exception){
            if(e is ForbidenCharException){
                // Expected exception, test passes !
            }else{
                fail("Expected ForbidenCharException")
            }
        }
    }


    //Birthday date converter

    /**
     * Tests the birthdayDateConverter method of the AddCandidateViewModel for a valid date string.
     */
    @Test
    fun test_birthdayDateConverter_returnsNull_forEmptyDateString() {
        val dateString = ""
        val result = viewModel.birthdayDateConverter(dateString)
        assertNull(result)
    }

    /**
     * Tests the birthdayDateConverter method of the AddCandidateViewModel for an invalid date string.
     */
    @Test
    fun test_birthdayDateConverter_returnsNull_forInvalidDateFormat() {
        val dateString = "invalid_format"
        val result = viewModel.birthdayDateConverter(dateString)
        assertNull(result)
    }

    /**
     * Tests the birthdayDateConverter method of the AddCandidateViewModel for a valid date string.
     */
    @Test
    fun test_birthdayDateConverter_convertsValidDateString_toLocalDateTime() {
        val expectedDate = LocalDate.of(2024, 7, 2) // Today's date
        val dateString = "02/07/2024" // Format assumed by the converter dd/mm/yyyy
        val result = viewModel.birthdayDateConverter(dateString)
        assertNotNull(result) // Verify conversion didn't return null
        assertEquals(expectedDate, result?.toLocalDate())
    }







}