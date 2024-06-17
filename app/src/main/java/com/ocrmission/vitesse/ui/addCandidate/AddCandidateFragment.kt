package com.ocrmission.vitesse.ui.addCandidate

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.ocrmission.vitesse.R
import com.ocrmission.vitesse.databinding.FragmentAddcandidateBinding
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
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Add Candidate Fragment
 */
@AndroidEntryPoint
class AddCandidateFragment : Fragment() {
    private lateinit var binding: FragmentAddcandidateBinding
    private var candidatePhotoUri: String? = null
    private val addCandidateViewModel: AddCandidateViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddcandidateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //setup all listeners
        setupClickListeners()
        //setup text watchers
        setupTextWatchers()
        //setup top bar
        setupTopBar()
        //setup birthday picker
        setupBirthdayPicker()
    }



    //SETUP STUFF ---------------------------------------------------------------------


    /**
     * Method to setup all listeners
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun setupClickListeners() {

        //listener on the Image Background of the new candidate
        binding.candidateImageViewBackground.setOnTouchListener{v, event ->
            when(event.action){
                MotionEvent.ACTION_DOWN -> {
                    v.scaleX = 1.05f
                    v.scaleY = 1.05f
                    return@setOnTouchListener true }
                MotionEvent.ACTION_UP -> {
                    v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
                    selectImage()
                    return@setOnTouchListener true  }
                else -> {
                    v.scaleX = 1.0f
                    v.scaleY = 1.0f
                    return@setOnTouchListener false }
            }
        }

        //listener on the Image of the new candidate
        binding.candidateMiniImageView.setOnTouchListener{v, event ->
            when(event.action){
                MotionEvent.ACTION_DOWN -> {
                    v.scaleX = 0.95f
                    v.scaleY = 0.95f
                    return@setOnTouchListener true }
                MotionEvent.ACTION_UP -> {
                    v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
                    selectImage()
                    return@setOnTouchListener true}
                else -> {
                    v.scaleX = 1.0f
                    v.scaleY = 1.0f
                    return@setOnTouchListener false }
            }
        }

        //listener on save button
        binding.candidateSaveButton.setOnClickListener {
            clearAllFocus()
            saveCandidate()
        }

    }

    /**
     * Method to setup all text watchers on input fields
     */
    private fun setupTextWatchers() {

        binding.firstnameEdittext.addTextChangedListener { checkInputField(it.toString(), binding.firstnameEditTextLayout,1)}
        binding.firstnameEditTextLayout.isEndIconVisible = false // hide the end icon

        binding.lastnameEdittext.addTextChangedListener {  checkInputField(it.toString(), binding.lastnameEditTextLayout,2)}
        binding.lastnameEditTextLayout.isEndIconVisible = false // hide the end icon

        binding.mailEdittext.addTextChangedListener { checkInputField(it.toString(), binding.mailEditTextLayout,3)  }
        binding.mailEditTextLayout.isEndIconVisible = false // hide the end icon

        binding.phoneEdittext.addTextChangedListener { checkInputField(it.toString(), binding.phoneEditTextLayout,4)  }
        binding.phoneEditTextLayout.isEndIconVisible = false // hide the end icon

        binding.salaryEdittext.addTextChangedListener { checkInputField(it.toString(), binding.salaryEditTextLayout,5)  }
        binding.salaryEditTextLayout.isEndIconVisible = false // hide the end icon

        binding.noteEdittext.addTextChangedListener { checkInputField(it.toString(), binding.noteEditTextLayout,6)  }
        binding.noteEditTextLayout.isEndIconVisible = false // hide the end icon

    }

    /**
     * Method to setup the top bar
     */
    private fun setupTopBar(){
        binding.addCandidateToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
           //findNavController().popBackStack()
           //requireActivity().onBackPressed()
        }
    }


    /**
     * Method to setup the birthday picker
     */
    private fun setupBirthdayPicker(){

        val birthdayPickerClickListener = View.OnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.birthday_picker_title))
                .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
                .setTextInputFormat(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()))
                .build()

            datePicker.addOnPositiveButtonClickListener { selectedDate ->
                val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(selectedDate))
                binding.birthdayInputEdittext.setText(formattedDate)

                //check if the selected date is in future
                val calendar = Calendar.getInstance()
                if (selectedDate > calendar.timeInMillis) {
                    showError(getString(R.string.birthday_picker_future_date_error), binding.birthdayInputLayout)
                }else{
                    // Format date and set text to EditText
                    binding.birthdayCardview.isChecked = true
                    showSuccess(binding.birthdayInputLayout)
                }

            }
            //pop the datepicker dialog
            datePicker.show(getParentFragmentManager(), "birthday_datePicker") // Use fragment manager for compatibility
        }

        binding.birthdayCardview.setOnClickListener(birthdayPickerClickListener)
        binding.birthdayInputEdittext.setOnClickListener(birthdayPickerClickListener)


    }






    //INPUT VALIDATION STUFF ---------------------------------------------------------------------
    /**
     * Method to check if the input is valid via the viewmodel.
     * @param text the input text
     * @param textInputLayout the input layout
     * @param filterType the filter type on the input (1 - firstname, 2 - lastname, 3 - phone, 4 - mail, 5 - salary, 6 - note)
     */
    private fun checkInputField(text: String, textInputLayout: TextInputLayout,filterType: Int){
        try {
            if(addCandidateViewModel.validateInput(text,filterType)){
                showSuccess(textInputLayout)
            }
        }catch (e: Exception){
            when(e){
                is EmptyTextException -> {
                    showError(getEmptyTextErrorHintMessage(textInputLayout), textInputLayout)
                }
                is ForbidenCharException -> {
                    showError(getTextCharErrorHintMessage(textInputLayout), textInputLayout)
                }
                is EmailFormatException -> {
                    showError(getString(R.string.error_invalid_format_email_text), textInputLayout)
                }
                is PhoneLengthException -> {
                    showError(getString(R.string.error_invalid_phonelenght), textInputLayout)
                }
            }

        }
    }

    /**
     * Method to show an error input UI
     * @param message the error message
     * @param textInputLayout the input layout to update
     */
    private fun showError(message: String, textInputLayout: TextInputLayout) {
        textInputLayout.error = message
        textInputLayout.isErrorEnabled = true
        textInputLayout.isEndIconVisible = false
        textInputLayout.hintTextColor = ColorStateList.valueOf(resources.getColor(R.color.error_text_color, null))
        textInputLayout.boxStrokeColor = resources.getColor(R.color.error_text_color, null)
    }

    /**
     * Method to show a success input UI
     * @param textInputLayout the input layout to update
     */
    private fun showSuccess(textInputLayout: TextInputLayout) {
        textInputLayout.error = null
        textInputLayout.isEndIconVisible = true
        textInputLayout.hintTextColor = ColorStateList.valueOf(resources.getColor(R.color.valid_text_color, null))
        textInputLayout.boxStrokeColor = resources.getColor(R.color.valid_text_color, null)
    }


    /**
     * Method to get the error message for empty text input
     * @param textInputLayout the input layout parent
     * @return the error message associated with the input layout
     */
    private fun getEmptyTextErrorHintMessage(textInputLayout: TextInputLayout): String {
        var dynamictxt = ""
        when(textInputLayout){
            binding.firstnameEditTextLayout -> { dynamictxt = getString(R.string.error_empty_textInput_firstname_text)  }
            binding.lastnameEditTextLayout -> { dynamictxt = getString(R.string.error_empty_textInput_lastname_text)  }
            binding.mailEditTextLayout -> { dynamictxt = getString(R.string.error_empty_textInput_email_text)  }
            binding.phoneEditTextLayout -> { dynamictxt = getString(R.string.error_empty_textInput_phone_text)  }
            binding.salaryEditTextLayout -> { dynamictxt = getString(R.string.error_empty_textInput_salary_text)  }
        }
        return "$dynamictxt "+getString(R.string.error_empty_textInput_base_text)
    }

    /**
     * Method to get the error message for forbidden char input
     * @param textInputLayout the input layout parent
     * @return the error message associated with the input layout
     */
    private fun getTextCharErrorHintMessage(textInputLayout: TextInputLayout): String {
        return when(textInputLayout){
            binding.firstnameEditTextLayout -> {getString(R.string.error_forbidden_char_textInput_firstname_text)}
            binding.lastnameEditTextLayout -> {getString(R.string.error_forbidden_char_textInput_lastname_text)}
            binding.mailEditTextLayout -> {getString(R.string.error_forbidden_char_textInput_email_text)}
            binding.phoneEditTextLayout -> {getString(R.string.error_forbidden_char_textInput_phone_text)}
            binding.salaryEditTextLayout -> {getString(R.string.error_forbidden_char_textInput_salary_text)}
            else -> {""}
        }
    }


    //SAVE CANDIDATE -----------------------------------------
    /**
     * Method to save the new candidate
     */
    private fun saveCandidate(){
        //try to add to BDD
        try {
            addCandidateViewModel.addCandidate(Candidate(
                firstname = binding.firstnameEdittext.text.toString().lowercase().replaceFirstChar { it.titlecase() },
                lastname = binding.lastnameEdittext.text.toString().uppercase(),
                phone = binding.phoneEdittext.text.toString(),
                email = binding.mailEdittext.text.toString().lowercase(),
                birthday = addCandidateViewModel.birthdayDateConverter(binding.birthdayInputEdittext.text.toString()),
                salary = binding.salaryEdittext.text.toString().toIntOrNull()?:0,
                note = binding.noteEdittext.text.toString(),
                photoUri = candidatePhotoUri?:"URI_NOT_SET",
                isFavorite = false

            ))

            Snackbar.make(this.requireView(),"Candidate successfully added !" , Snackbar.LENGTH_SHORT).show()

            // Find the NavController and navigate back
            findNavController().navigateUp()

        }catch (e: Exception){
            Log.d("saveCandidate", "saveCandidate: Exception : ${e.toString()}")
            //Error handling
            val errorMsgToDisplay =
            when(e){
                is MissingFirstNameException -> {getString(R.string.error_missing_textInput_firstname_text)}
                is MissingLastNameException -> {getString(R.string.error_missing_textInput_lastname_text)}
                is MissingEmailException -> {getString(R.string.error_missing_textInput_email_text)}
                is MissingPhoneException -> {getString(R.string.error_missing_textInput_phone_text)}
                is MissingBirthException -> {getString(R.string.error_missing_textInput_birthdate_text)}
                else -> {getString(R.string.error_missing_textInput_Generic_text)}
            }
            Snackbar.make(this.requireView(),errorMsgToDisplay , Snackbar.LENGTH_SHORT).show()
        }

    }


    //Clearing Focus -------------------------------------------------------------------------
    /**
     * Method to clear all focus from the input fields
     */
    private fun clearAllFocus(){
        binding.firstnameEdittext.clearFocus()
        binding.lastnameEdittext.clearFocus()
        binding.phoneEdittext.clearFocus()
        binding.mailEdittext.clearFocus()
        binding.salaryEdittext.clearFocus()
        binding.noteEdittext.clearFocus()
    }


    //CANDIDATE PICTURE STUFF ---------------------------------------------------------------------
    /**
     * Method to register a photo picker activity launcher
     */
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            candidatePhotoUri = uri.toString()
            Glide.with(this).load(uri).apply(bitmapTransform(jp.wasabeef.glide.transformations.BlurTransformation(15,3))).into(binding.candidateImageViewBackground)
            Glide.with(this).load(uri).into(binding.candidateMiniImageView)
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    /**
     * Method to select images from the user gallery
     */
    private fun selectImage() {
        // Launch the photo picker and let the user choose only images.
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

    }


}