package com.ocrmission.vitesse.ui.addCandidate

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Rect
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
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

    private var showButtonAnimation: ObjectAnimator? = null
    private var hideButtonAnimation: ObjectAnimator? = null
    private var isButtonVisible = false



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddcandidateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //setup text watchers
        setupTextWatchers()
        //setup top bar
        setupTopBar()
        //setup birthday picker
        setupBirthdayPicker()

        //Initialisation click listeners
        initClickListeners()
        //Initialisation of the focus listeners
        initFocusingListener()
        //Initialisation of the scroll change listeners
        initScrollChangeListener()

    }



//SETUP STUFF ---------------------------------------------------------------------


    /**
     * Method to setup all listeners
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun initClickListeners() {

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


        //--- Listener on all section cards click
        val cardTouchListeners = View.OnClickListener {
            val touchedCard =
                when(it.id){
                    R.id.name_cardview -> {binding.firstnameEdittext}
                    R.id.phone_cardview -> {binding.phoneEdittext}
                    R.id.mail_cardview -> {binding.mailEdittext}
                    R.id.salary_cardview -> {binding.salaryEdittext}
                    R.id.notes_cardview -> {binding.noteEdittext}
                    else -> {null}
                }
            requestKeyboard(touchedCard)
        }
        binding.nameCardview.setOnClickListener(cardTouchListeners)
        binding.phoneCardview.setOnClickListener(cardTouchListeners)
        binding.mailCardview.setOnClickListener(cardTouchListeners)
        binding.salaryCardview.setOnClickListener(cardTouchListeners)
        binding.notesCardview.setOnClickListener(cardTouchListeners)


        //--- Listener on keyboard action button
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
        binding.phoneEdittext.setOnEditorActionListener(editorActionListener)
        binding.mailEdittext.setOnEditorActionListener(editorActionListener)
        binding.firstnameEdittext.setOnEditorActionListener(editorActionListener)
        binding.lastnameEdittext.setOnEditorActionListener(editorActionListener)
        binding.birthdayInputEdittext.setOnEditorActionListener(editorActionListener)
        binding.salaryEdittext.setOnEditorActionListener(editorActionListener)
        binding.noteEdittext.setOnEditorActionListener(editorActionListener)


    }

    /**
     * Method to setup all text watchers on input fields
     */
    private fun setupTextWatchers() {
        binding.firstnameEdittext.addTextChangedListener { checkInputField(it.toString(), binding.firstnameEditTextLayout,1)}
        binding.lastnameEdittext.addTextChangedListener {  checkInputField(it.toString(), binding.lastnameEditTextLayout,2)}
        binding.mailEdittext.addTextChangedListener { checkInputField(it.toString(), binding.mailEditTextLayout,3)  }
        binding.phoneEdittext.addTextChangedListener { checkInputField(it.toString(), binding.phoneEditTextLayout,4)  }
        binding.salaryEdittext.addTextChangedListener { checkInputField(it.toString(), binding.salaryEditTextLayout,5)  }
        binding.noteEdittext.addTextChangedListener { checkInputField(it.toString(), binding.noteEditTextLayout,6)  }
    }

    /**
     * Method to setup the top bar
     */
    private fun setupTopBar(){
        binding.addCandidateToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
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
            //pop the date picker dialog
            datePicker.show(getParentFragmentManager(), "birthday_datePicker") // Use fragment manager for compatibility
        }

        binding.birthdayCardview.setOnClickListener(birthdayPickerClickListener)
        binding.birthdayInputEdittext.setOnClickListener(birthdayPickerClickListener)


    }



// [LISTENERS] --------------------------------------------------------------------


    /**
     * Method to setup the listener on the scroll view + the save button anchor view animations
     */
    private fun initScrollChangeListener() {
        // Set initial button visibility (consider removing if using animation initially)
        binding.candidateSaveButton.translationY = 300f

        showButtonAnimation = ObjectAnimator.ofFloat(binding.candidateSaveButton, "translationY", 300f, 0f)
        hideButtonAnimation = ObjectAnimator.ofFloat(binding.candidateSaveButton, "translationY", 0f, 300f)

        // Set animation properties
        showButtonAnimation?.duration = 200L // Adjust duration as needed
        showButtonAnimation?.interpolator = DecelerateInterpolator() // Example interpolator
        hideButtonAnimation?.duration = 100L
        hideButtonAnimation?.interpolator = AccelerateDecelerateInterpolator()
        //Set Scrolling listener
        binding.addCandidateScrollview.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            //check if the anchor is visible on screen
            isAnchorViewVisible()
            //Background Picture translation effect (DECO)
            binding.candidateImageViewBackground.translationY = scrollY/1f
            // BackgroundPicture zoom scaling on scroll (DECO)
            val newScale = (1f + scrollY / 2000f)
            val currentScale = maxOf(1f, minOf(3f, newScale))
            binding.candidateImageViewBackground.scaleX = currentScale
            binding.candidateImageViewBackground.scaleY = currentScale

        }
    }


    /**
     * Methode to setup the focus listener on all edittext and call the autoscroll and card animation.
     */
    private fun initFocusingListener(){
        val focusListener = View.OnFocusChangeListener { v, hasFocus ->
            val targetView: View? =
                when (v.id) {
                    //firstname
                    R.id.firstname_edittext -> { binding.nameCardview }
                    //lastname
                    R.id.lastname_edittext -> { binding.nameCardview }
                    //phone
                    R.id.phone_edittext -> { binding.phoneCardview }
                    //mail
                    R.id.mail_edittext -> { binding.mailCardview }
                    //birthday
                    R.id.birthday_input_edittext -> { binding.birthdayCardview }
                    //salary
                    R.id.salary_edittext -> { binding.salaryCardview }
                    //notes
                    R.id.note_edittext -> { binding.notesCardview }
                    else -> {null}
                }
            // autoscroll to section card and zoom up card
            if(targetView!=null){
              //  sectionCardSizer((targetView as MaterialCardView), !hasFocus)
                autoscrollAtFocusChange(targetView,hasFocus)
            }
        }
        //setup focuslistener on all edittext
        binding.phoneEdittext.onFocusChangeListener = focusListener
        binding.mailEdittext.onFocusChangeListener = focusListener
        binding.firstnameEdittext.onFocusChangeListener = focusListener
        binding.lastnameEdittext.onFocusChangeListener = focusListener
        binding.birthdayInputEdittext.onFocusChangeListener = focusListener
        binding.salaryEdittext.onFocusChangeListener = focusListener
        binding.noteEdittext.onFocusChangeListener = focusListener
    }


// [SCROLLER]  --------------------------------------------------------
    /**
     * Methode to autoscroll to the section card over the keyboard position when the card get the focus
     * @param view the card view who get the focus
     * @param hasFocus the focus state of the card view
     */
    private fun autoscrollAtFocusChange(view: View, hasFocus: Boolean) {
        if (hasFocus) {
            val offsetDyn =
                when(view.id){
                    //Name Offset - (layout is taller than other with 2 text input fields, we need to adapt the offset at + 3/4 of the card height )
                    R.id.name_cardview -> {(3*(view.height/4))}
                    //Mail Offset - (this card had the same size of phone card, but the keyboard had an over-bar, we need to adapt the offset at + 3/5 of the card height)
                    R.id.mail_cardview -> {(3*(view.height/5))}
                    //Notes Offset - ( + 4/5 of the card height)
                    R.id.notes_cardview -> { (4*(view.height/5))}
                    else -> {0} //Classic card case - (no over-bar on keyboard) - used by : phone / birthday / salary
                }
            //Autoscroll the section card to the good position just over the keyboard
            binding.addCandidateScrollview.scrollTo(0,  (view.top - binding.addCandidateScrollview.height / 2) + offsetDyn)
        }
    }


// [SAVE BUTTON ANCHOR]  --------------------------------------------------------

    /**
     * Method to check if the save button scrolling anchor is visible on screen
     * @return true if the anchor is visible, false otherwise
     */
    private fun isAnchorViewVisible(): Boolean {
        val rect = Rect()
        binding.candidateSaveButtonAnchor.getGlobalVisibleRect(rect)
        val isVisible = rect.bottom > 0 && rect.top < binding.addCandidateScrollview.height
        // Check if anchor is visible and button is hidden
        if (isVisible && !isButtonVisible) {
            showButtonAnimation?.start()
            isButtonVisible = true
        } else if (!isVisible && isButtonVisible) {
            // Check if anchor is hidden and button is shown
            hideButtonAnimation?.start()
            isButtonVisible = false
        }
        return isVisible
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
            //check if the input is valid
            if(addCandidateViewModel.validateInput(text,filterType)) showSuccess(textInputLayout)
        }catch (e: Exception){
            when(e){
                is EmptyTextException -> {
                    //filtering Notes and salary special case (empty field is allowed)
                    if(filterType == 6 || filterType == 5)showSuccess(textInputLayout)
                    else showError(getEmptyTextErrorHintMessage(textInputLayout), textInputLayout)
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
        //mark the cardview as not checked
        setCardViewChecked(textInputLayout,false)
    }

    /**
     * Method to show a success input UI
     * @param textInputLayout the input layout to update
     */
    private fun showSuccess(textInputLayout: TextInputLayout) {
        textInputLayout.error = null
        textInputLayout.hintTextColor = ColorStateList.valueOf(resources.getColor(R.color.valid_text_color, null))
        textInputLayout.boxStrokeColor = resources.getColor(R.color.valid_text_color, null)
        //mark the cardview as checked
        setCardViewChecked(textInputLayout,true)
    }

    /**
     * Method to set the checked state of the cardview
     * @param textInputLayout the input layout to update his cardview
     * @param isChecked the checked state
     */
    private fun setCardViewChecked(textInputLayout: TextInputLayout, isChecked: Boolean){
        when(textInputLayout){
            binding.salaryEditTextLayout-> {binding.salaryCardview.isChecked = isChecked}
            binding.mailEditTextLayout-> {binding.mailCardview.isChecked = isChecked}
            binding.phoneEditTextLayout-> {binding.phoneCardview.isChecked = isChecked}
            binding.lastnameEditTextLayout-> {binding.nameCardview.isChecked = isChecked}
            binding.noteEditTextLayout -> {binding.notesCardview.isChecked = isChecked}
            else -> { }
        }
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
            parentFragment?.let { Snackbar.make(it.requireView(),getString(R.string.snack_message_add_success) , Snackbar.LENGTH_SHORT).show() }

            // Find the NavController and navigate back
            findNavController().navigateUp()

        }catch (e: Exception){
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
            Glide.with(this).load(uri).into(binding.candidateImageViewBackground)
            Glide.with(this).load(uri).into(binding.candidateMiniImageView)
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


//KEYBOARD -------------------------------------------------------------------------
    /**
     * Methode to request the keyboard and give the focus to the good edittext
     * (used on card section click)
     * @param view the view who request the keyboard
     */
    private fun requestKeyboard(view: View?) {
        val  inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        view?.requestFocus()
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

}