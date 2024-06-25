package com.ocrmission.vitesse.ui.editCandidate

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.card.MaterialCardView
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.ocrmission.vitesse.R
import com.ocrmission.vitesse.databinding.FragmentEditCandidateBinding
import com.ocrmission.vitesse.domain.Candidate
import com.ocrmission.vitesse.ui.addCandidate.exceptions.EmailFormatException
import com.ocrmission.vitesse.ui.addCandidate.exceptions.EmptyTextException
import com.ocrmission.vitesse.ui.addCandidate.exceptions.ForbidenCharException
import com.ocrmission.vitesse.ui.addCandidate.exceptions.MissingBirthException
import com.ocrmission.vitesse.ui.addCandidate.exceptions.MissingEmailException
import com.ocrmission.vitesse.ui.addCandidate.exceptions.MissingFirstNameException
import com.ocrmission.vitesse.ui.addCandidate.exceptions.MissingLastNameException
import com.ocrmission.vitesse.ui.addCandidate.exceptions.MissingPhoneException
import com.ocrmission.vitesse.ui.addCandidate.exceptions.PhoneLengthException
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Edit Candidate Fragment
 */
@AndroidEntryPoint
class EditCandidateFragment : Fragment() {
    private lateinit var binding: FragmentEditCandidateBinding
    private val editCandidateViewModel: EditCandidateViewModel by viewModels()

    private val args: EditCandidateFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditCandidateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //retrieve the candidate id from the arguments
        retrieveCandidateID()
        //observe the candidate flow from the viewmodel
        setupObservers()
        //setup the toolbar
        setupToolBar()

        //setup all listeners
        setupClickListeners()
        //setup text watchers
        setupTextWatchers()
        //setup birthday picker
        setupBirthdayPicker()

        //for some UX design
        sectionCardsMinimizator()
        focusingAnimator()
        setupImeActionListener()
    }

//SETUP STUFF --------------------------------------------------------------

    /**
     * Retrieve the candidate id from the navigation arguments
     */
    private fun retrieveCandidateID() {
        val candidateId = args.CandidateId
        editCandidateViewModel.fetchingCandidateById(candidateId)
    }

    /**
     * Observe the candidate flow from the viewmodel
     */
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            editCandidateViewModel.candidate.collect {
                updateUI(it)
            }
        }
    }

    /**
     * Method to setup the top bar of the screen
     */
    private fun setupToolBar(){
        //set all listener on the toolbar buttons
        binding.editToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }


    /**
     * Method to setup all listeners
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun setupClickListeners() {

        //listener on the Image Background of the new candidate
        binding.editcandidateImageViewBackground.setOnTouchListener{v, event ->
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
        binding.editcandidateMiniImageView.setOnTouchListener{v, event ->
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
        binding.editcandidateSaveButton.setOnClickListener {
            saveUpdatedCandidate()
        }

    }


    /**
     * Method to setup all text watchers on input fields
     */
    private fun setupTextWatchers() {
        binding.editfirstnameEdittext.addTextChangedListener { checkInputField(it.toString(), binding.editfirstnameEditTextLayout,1)}
        binding.editlastnameEdittext.addTextChangedListener {  checkInputField(it.toString(), binding.editlastnameEditTextLayout,2)}
        binding.editmailEdittext.addTextChangedListener { checkInputField(it.toString(), binding.editmailEditTextLayout,3)  }
        binding.editphoneEdittext.addTextChangedListener { checkInputField(it.toString(), binding.editphoneEditTextLayout,4)  }
        binding.editsalaryEdittext.addTextChangedListener { checkInputField(it.toString(), binding.editsalaryEditTextLayout,5)  }
        binding.editnoteEdittext.addTextChangedListener { checkInputField(it.toString(), binding.editnoteEditTextLayout,6)  }
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
                binding.editbirthdayInputEdittext.setText(formattedDate)

                //check if the selected date is in future
                val calendar = Calendar.getInstance()
                if (selectedDate > calendar.timeInMillis) {
                    showError(getString(R.string.birthday_picker_future_date_error), binding.editbirthdayInputLayout)
                }else{
                    // Format date and set text to EditText
                    showSuccess(binding.editbirthdayInputLayout)
                }

            }
            //pop the datepicker dialog
            datePicker.show(getParentFragmentManager(), "birthday_datePicker") // Use fragment manager for compatibility
        }

        binding.editbirthdayCardview.setOnClickListener(birthdayPickerClickListener)
        binding.editbirthdayInputEdittext.setOnClickListener(birthdayPickerClickListener)


    }

//INPUT VALIDATION STUFF ---------------------------------------------------------------------

    /**
     * Method to check if the input is valid via the viewmodel.
     * @param text the input text
     * @param textInputLayout the input layout
     * @param filterType the filter type on the input (1 - firstname, 2 - lastname, 3 - phone, 4 - mail, 5 - salary, 6 - note)
     */
    private fun checkInputField(text: String, textInputLayout: TextInputLayout, filterType: Int){
        try {
            //check if the input is valid
            if(editCandidateViewModel.validateInput(text,filterType)) showSuccess(textInputLayout)

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
        textInputLayout.hintTextColor = ColorStateList.valueOf(resources.getColor(R.color.error_text_color, null))
        textInputLayout.boxStrokeColor = resources.getColor(R.color.error_text_color, null)
    }

    /**
     * Method to show a success input UI
     * @param textInputLayout the input layout to update
     */
    private fun showSuccess(textInputLayout: TextInputLayout) {
        textInputLayout.error = null
        textInputLayout.hintTextColor = ColorStateList.valueOf(resources.getColor(R.color.valid_text_color, null))
        textInputLayout.boxStrokeColor = resources.getColor(R.color.valid_text_color, null)


    }


//ERROR MESSAGE STUFF ---------------------------------------------------------------------

    /**
     * Method to get the error message for empty text input
     * @param textInputLayout the input layout parent
     * @return the error message associated with the input layout
     */
    private fun getEmptyTextErrorHintMessage(textInputLayout: TextInputLayout): String {
        var dynamictxt = ""
        when(textInputLayout){
            binding.editfirstnameEditTextLayout -> { dynamictxt = getString(R.string.error_empty_textInput_firstname_text)  }
            binding.editlastnameEditTextLayout -> { dynamictxt = getString(R.string.error_empty_textInput_lastname_text)  }
            binding.editmailEditTextLayout -> { dynamictxt = getString(R.string.error_empty_textInput_email_text)  }
            binding.editphoneEditTextLayout -> { dynamictxt = getString(R.string.error_empty_textInput_phone_text)  }
            binding.editsalaryEditTextLayout -> { dynamictxt = getString(R.string.error_empty_textInput_salary_text)  }
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
            binding.editfirstnameEditTextLayout -> {getString(R.string.error_forbidden_char_textInput_firstname_text)}
            binding.editlastnameEditTextLayout -> {getString(R.string.error_forbidden_char_textInput_lastname_text)}
            binding.editmailEditTextLayout -> {getString(R.string.error_forbidden_char_textInput_email_text)}
            binding.editphoneEditTextLayout -> {getString(R.string.error_forbidden_char_textInput_phone_text)}
            binding.editsalaryEditTextLayout -> {getString(R.string.error_forbidden_char_textInput_salary_text)}
            else -> {""}
        }
    }




//UPDATE STUFF -------------------------------------------------------

    /**
     * Update the UI with the candidate data
     * @param candidate the last updated candidate data
     */
    private fun updateUI(candidate: Candidate) {
        binding.editfirstnameEdittext.setText(candidate.firstname)
        binding.editlastnameEdittext.setText(candidate.lastname)
        binding.editphoneEdittext.setText(candidate.phone)
        binding.editmailEdittext.setText(candidate.email)
        binding.editsalaryEdittext.setText(candidate.salary.toString())
        binding.editnoteEdittext.setText(candidate.note)
        //birthday
        binding.editbirthdayInputEdittext.setText(editCandidateViewModel.formatDateBirthday(candidate.birthday))
        //picture
        val uri = Uri.parse(candidate.photoUri)
        Glide.with(this).load(uri).apply(RequestOptions.bitmapTransform(BlurTransformation(15, 3))).into(binding.editcandidateImageViewBackground)
        Glide.with(this).load(uri).into(binding.editcandidateMiniImageView)
    }


    /**
     * Method to save the updated candidate
     */
    private fun saveUpdatedCandidate(){
        try{
            editCandidateViewModel.updateCandidate(Candidate(
                id = editCandidateViewModel.candidate.value.id,
                firstname = binding.editfirstnameEdittext.text.toString().lowercase().replaceFirstChar { it.titlecase() },
                lastname = binding.editlastnameEdittext.text.toString().uppercase(),
                phone = binding.editphoneEdittext.text.toString(),
                email = binding.editmailEdittext.text.toString().lowercase(),
                birthday = editCandidateViewModel.birthdayDateConverter(binding.editbirthdayInputEdittext.text.toString()),
                salary = binding.editsalaryEdittext.text.toString().toIntOrNull()?:0,
                note = binding.editnoteEdittext.text.toString(),
                photoUri = editCandidateViewModel.candidate.value.photoUri,
                isFavorite = editCandidateViewModel.candidate.value.isFavorite

            ))
            Snackbar.make(requireView(),
                getString(R.string.snack_message_edit_update) , Snackbar.LENGTH_SHORT).show()
            // Find the NavController and navigate back
            findNavController().navigateUp()

        }catch (e: Exception){
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



//CANDIDATE PICTURE STUFF ---------------------------------------------------------------------
    /**
     * Method to register a photo picker activity launcher
     */
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            editCandidateViewModel.candidate.value.photoUri = uri.toString()

            Glide.with(this).load(uri).apply(
                RequestOptions.bitmapTransform(
                    BlurTransformation(
                        15,
                        3
                    )
                )
            ).into(binding.editcandidateImageViewBackground)
            Glide.with(this).load(uri).into(binding.editcandidateMiniImageView)
        } else {
            //no media selected !
        }
    }

    /**
     * Method to select images from the user gallery
     */
    private fun selectImage() {
        // Launch the photo picker and let the user choose only images.
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

    }


//EXPLORATION AND FUN -------------------------------------------------------------------
    /**
     * Methode to set animation on section cards focus change.
     */
    private fun focusingAnimator(){
        val focusListener = View.OnFocusChangeListener { v, hasFocus ->
            when (v.id) {
                R.id.editfirstname_edittext -> { sectionCardSizer(binding.editnameCardview, !hasFocus) }
                R.id.editlastname_edittext -> { sectionCardSizer(binding.editnameCardview, !hasFocus) }
                R.id.editphone_edittext -> { sectionCardSizer(binding.editphoneCardview, !hasFocus) }
                R.id.editmail_edittext -> { sectionCardSizer(binding.editmailCardview, !hasFocus) }
                R.id.editbirthday_input_edittext -> { sectionCardSizer(binding.editbirthdayCardview, !hasFocus) }
                R.id.editsalary_edittext -> { sectionCardSizer(binding.editsalaryCardview, !hasFocus) }
                R.id.editnote_edittext -> { sectionCardSizer(binding.editnotesCardview, !hasFocus) }
            }
        }
        //setup focuslistener on all edittext
        binding.editphoneEdittext.onFocusChangeListener = focusListener
        binding.editmailEdittext.onFocusChangeListener = focusListener
        binding.editfirstnameEdittext.onFocusChangeListener = focusListener
        binding.editlastnameEdittext.onFocusChangeListener = focusListener
        binding.editbirthdayInputEdittext.onFocusChangeListener = focusListener
        binding.editsalaryEdittext.onFocusChangeListener = focusListener
        binding.editnoteEdittext.onFocusChangeListener = focusListener
    }

    /**
     * Methode to change the size of section card with focus change.
     * @param who the card to change the size
     * @param minimized the new "target" size state of the card
     * @param duration the animation duration
     */
    private fun sectionCardSizer(who: MaterialCardView, minimized: Boolean, duration: Long = 300){
        if(minimized){
            who.animate().scaleX(0.90f).scaleY(0.90f).setDuration(duration).start()
        }else{
            who.animate().scaleX(1.0f).scaleY(1.0f).setDuration(duration).start()
        }
    }

    /**
     * Methode to minimize all section cards.
     */
    private fun sectionCardsMinimizator(){
        sectionCardSizer(binding.editnameCardview, true, 0)
        sectionCardSizer(binding.editphoneCardview, true, 0)
        sectionCardSizer(binding.editmailCardview, true, 0)
        sectionCardSizer(binding.editbirthdayCardview, true, 0)
        sectionCardSizer(binding.editsalaryCardview, true, 0)
        sectionCardSizer(binding.editnotesCardview, true, 0)
    }

    /**
     * Methode to setup the ime action button listener
     */
    private fun setupImeActionListener(){
        val editorActionListener = object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // edition ok clean the focus
                    v?.clearFocus() // Libérer le focus de l'EditText
                    //hide the keyboard
                    val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v?.windowToken, 0)
                    return true
                }
                return false
            }
        }
        binding.editphoneEdittext.setOnEditorActionListener(editorActionListener)
        binding.editmailEdittext.setOnEditorActionListener(editorActionListener)
        binding.editfirstnameEdittext.setOnEditorActionListener(editorActionListener)
        binding.editlastnameEdittext.setOnEditorActionListener(editorActionListener)
        binding.editbirthdayInputEdittext.setOnEditorActionListener(editorActionListener)
        binding.editsalaryEdittext.setOnEditorActionListener(editorActionListener)
        binding.editnoteEdittext.setOnEditorActionListener(editorActionListener)

    }





}