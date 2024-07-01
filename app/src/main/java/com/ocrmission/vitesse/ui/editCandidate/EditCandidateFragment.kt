package com.ocrmission.vitesse.ui.editCandidate

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Rect
import android.net.Uri
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
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
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Edit Candidate Fragment - this fragment represent the edit candidate screen used to edit the candidate data.
 */
@AndroidEntryPoint
class EditCandidateFragment : Fragment() {

    private lateinit var binding: FragmentEditCandidateBinding
    private val editCandidateViewModel: EditCandidateViewModel by viewModels()
    private val args: EditCandidateFragmentArgs by navArgs()


// [LIFE CYCLE] --------------------------------------------------------------

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
        //Initialisation of the toolbar
        initToolBar()
        //Initialisation of the Click listeners
        initClickListeners()
        //Initialisation of the text watchers listeners
        initTextWatchers()
        //Initialisation of the focus listeners
        initFocusingListener()
        //Initialisation of the scroll change listeners
        initScrollChangeListener()
        //Initialisation of the birthday picker
        initBirthdayPicker()
        //pre-scale all section cards
        sectionCardsMinimizer()
    }


// [INIT] --------------------------------------------------------------

    /**
     * Retrieve the candidate id from the navigation arguments
     */
    private fun retrieveCandidateID() {
        val candidateId = args.CandidateId
        editCandidateViewModel.fetchingCandidateById(candidateId)
    }

    /**
     * Observe the candidate flow of the viewmodel
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
    private fun initToolBar(){
        //set all listener on the toolbar buttons
        binding.editToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }


// [LISTENERS] ----------------------------------------------------------------

    /**
     * Method to setup all views listeners
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun initClickListeners() {

        //--- Listener on the Photos of the new candidate (minified/background)
        val candidatePictureTouchListeners = View.OnTouchListener { view, motionEvent ->
            when(motionEvent.action){
                MotionEvent.ACTION_DOWN -> {
                    view.scaleX = 1.05f
                    view.scaleY = 1.05f
                    return@OnTouchListener true
                }
                MotionEvent.ACTION_UP -> {
                    view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
                    selectImage()
                    return@OnTouchListener true
                }
                else -> {
                    view.scaleX = 1.0f
                    view.scaleY = 1.0f
                    return@OnTouchListener false
                }
            }
        }
        binding.editcandidateImageViewBackground.setOnTouchListener(candidatePictureTouchListeners)
        binding.editcandidateMiniImageView.setOnTouchListener(candidatePictureTouchListeners)

        //--- Listener on save button
        binding.editcandidateSaveButton.setOnClickListener {
            saveUpdatedCandidate()
        }

        //--- Listener on all section cards click
        val cardTouchListeners = View.OnClickListener {
            val touchedCard =
            when(it.id){
                R.id.editname_cardview -> {binding.editfirstnameEdittext}
                R.id.editphone_cardview -> {binding.editphoneEdittext}
                R.id.editmail_cardview -> {binding.editmailEdittext}
                R.id.editsalary_cardview -> {binding.editsalaryEdittext}
                R.id.editnotes_cardview -> {binding.editnoteEdittext}
                else -> {null}
            }
            requestKeyboard(touchedCard)
        }
        binding.editnameCardview.setOnClickListener(cardTouchListeners)
        binding.editphoneCardview.setOnClickListener(cardTouchListeners)
        binding.editmailCardview.setOnClickListener(cardTouchListeners)
        binding.editsalaryCardview.setOnClickListener(cardTouchListeners)
        binding.editnotesCardview.setOnClickListener(cardTouchListeners)

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
        binding.editphoneEdittext.setOnEditorActionListener(editorActionListener)
        binding.editmailEdittext.setOnEditorActionListener(editorActionListener)
        binding.editfirstnameEdittext.setOnEditorActionListener(editorActionListener)
        binding.editlastnameEdittext.setOnEditorActionListener(editorActionListener)
        binding.editbirthdayInputEdittext.setOnEditorActionListener(editorActionListener)
        binding.editsalaryEdittext.setOnEditorActionListener(editorActionListener)
        binding.editnoteEdittext.setOnEditorActionListener(editorActionListener)

    }

    /**
     * Method to setup all text watchers on input fields
     */
    private fun initTextWatchers() {
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
    private fun initBirthdayPicker(){
        val birthdayPickerClickListener = View.OnClickListener {
            //clear all others focus, and scroll to the birthday picker section
            requireView().clearFocus()
            autoscrollAtFocusChange(binding.editbirthdayCardview,true)
            // build the dialog
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.birthday_picker_title))
                .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
                .setTextInputFormat(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()))
                .build()
            //add the positive button listener of the dialog
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
            //pop the date picker dialog
            datePicker.show(getParentFragmentManager(), "birthday_datePicker")
        }
        //set the click listener on the birthday picker views (card + text input field)
        binding.editbirthdayCardview.setOnClickListener(birthdayPickerClickListener)
        binding.editbirthdayInputEdittext.setOnClickListener(birthdayPickerClickListener)
    }

    /**
     * Methode to setup the focus listener on all edittext and call the autoscroll and card animation.
     */
    private fun initFocusingListener(){
        val focusListener = View.OnFocusChangeListener { v, hasFocus ->
            val targetView: View? =
                when (v.id) {
                    //firstname
                    R.id.editfirstname_edittext -> { binding.editnameCardview }
                    //lastname
                    R.id.editlastname_edittext -> { binding.editnameCardview }
                    //phone
                    R.id.editphone_edittext -> { binding.editphoneCardview }
                    //mail
                    R.id.editmail_edittext -> { binding.editmailCardview }
                    //birthday
                    R.id.editbirthday_input_edittext -> { binding.editbirthdayCardview }
                    //salary
                    R.id.editsalary_edittext -> { binding.editsalaryCardview }
                    //notes
                    R.id.editnote_edittext -> { binding.editnotesCardview }
                    else -> {null}
                }
            // autoscroll to section card and zoom up card
            if(targetView!=null){
                sectionCardSizer((targetView as MaterialCardView), !hasFocus)
                autoscrollAtFocusChange(targetView,hasFocus)
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
     * Method to setup the listener on the scroll view + the save button anchor view animations
     */
    private fun initScrollChangeListener() {
        // Set initial button visibility (consider removing if using animation initially)
        binding.editcandidateSaveButton.translationY = 300f

        showButtonAnimation = ObjectAnimator.ofFloat(binding.editcandidateSaveButton, "translationY", 300f, 0f)
        hideButtonAnimation = ObjectAnimator.ofFloat(binding.editcandidateSaveButton, "translationY", 0f, 300f)

        // Set animation properties
        showButtonAnimation?.duration = 200L // Adjust duration as needed
        showButtonAnimation?.interpolator = DecelerateInterpolator() // Example interpolator
        hideButtonAnimation?.duration = 100L
        hideButtonAnimation?.interpolator = AccelerateDecelerateInterpolator()
        //Set Scrolling listener
        binding.editCandidateScrollview.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            //check if the anchor is visible on screen
            isAnchorViewVisible()
            //Background Picture translation effect (DECO)
            binding.editcandidateImageViewBackground.translationY = scrollY/1f
            // BackgroundPicture zoom scaling on scroll (DECO)
            val newScale = (1f + scrollY / 2000f)
            val currentScale = maxOf(1f, minOf(3f, newScale))
            binding.editcandidateImageViewBackground.scaleX = currentScale
            binding.editcandidateImageViewBackground.scaleY = currentScale
            //Section Cards zoom Scaling on scroll (DECO)
            scrollUnZoom(binding.editnameCardview, scrollY, 14000f, 0.8f)
            scrollUnZoom(binding.editphoneCardview, scrollY, 18000f, 0.8f)
            scrollUnZoom(binding.editmailCardview, scrollY, 22000f, 0.8f)
            scrollUnZoom(binding.editbirthdayCardview, scrollY, 26000f, 0.8f)
            scrollUnZoom(binding.editsalaryCardview, scrollY, 30000f, 0.8f)
            scrollUnZoom(binding.editnotesCardview, scrollY, 34000f, 0.85f)
        }
    }


// [UI UPDATE] -------------------------------------------------------

    /**
     * Update the UI with the candidate data
     * @param candidate the last updated candidate data
     */
    private fun updateUI(candidate: Candidate) {
        //Update all the input fields with the candidate data
        binding.editfirstnameEdittext.setText(candidate.firstname)
        binding.editlastnameEdittext.setText(candidate.lastname)
        binding.editphoneEdittext.setText(candidate.phone)
        binding.editmailEdittext.setText(candidate.email)
        binding.editsalaryEdittext.setText(candidate.salary.toString())
        binding.editnoteEdittext.setText(candidate.note)
        //Update the birthday section
        binding.editbirthdayInputEdittext.setText(editCandidateViewModel.formatDateBirthday(candidate.birthday))
        //Update the photos of the candidate
        val uri = Uri.parse(candidate.photoUri)
        Glide.with(this).load(uri).error(R.drawable.placeholdercustom_b).into(binding.editcandidateImageViewBackground)
        Glide.with(this).load(uri).error(R.drawable.placeholdercustom_man).into(binding.editcandidateMiniImageView)
    }


// [INPUT FIELDS VALIDATION]  ---------------------------------------------------------------------

    /**
     * Method to check if the input is valid via the viewmodel.
     * @param text the input text
     * @param textInputLayout the input layout
     * @param filterType the filter type on the input (1 - firstname, 2 - lastname, 3 - phone, 4 - mail, 5 - salary, 6 - note)
     */
    private fun checkInputField(text: String, textInputLayout: TextInputLayout, filterType: Int){
        try {
            //check if the input is valid via the dedicated jobbing method in the viewmodel
            if(editCandidateViewModel.validateInput(text,filterType)) showSuccess(textInputLayout)
        }catch (e: Exception){
            //if the input is invalid a custom exception is raised, we showing the error message to the user
            when(e){
                is EmptyTextException -> {
                    //filtering Notes and salary special case (empty field is allowed)
                    if(filterType == 6 || filterType == 5)showSuccess(textInputLayout)
                    else showError(getEmptyTextErrorHintMessage(textInputLayout), textInputLayout)
                }
                is ForbidenCharException -> {showError(getTextCharErrorHintMessage(textInputLayout), textInputLayout)}
                is EmailFormatException -> {showError(getString(R.string.error_invalid_format_email_text), textInputLayout)}
                is PhoneLengthException -> {showError(getString(R.string.error_invalid_phonelenght), textInputLayout)}
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


//[ERRORS MESSAGES]  ---------------------------------------------------------------------

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


// [SAVE CANDIDATE] -------------------------------------------------------

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
            // Show a snackbar to confirm the update (need this specific way to do , because the navController kill the current view)
            parentFragment?.let { Snackbar.make(it.requireView(),getString(R.string.snack_message_edit_update) , Snackbar.LENGTH_SHORT).show() }
            // Find the NavController and navigate back
            findNavController().navigateUp()
        }catch (e: Exception){
            //if the input is invalid a custom exception is raised, we showing the error message to the user
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


// [CANDIDATE PICTURE] ----------------------------------------------------
    /**
     * Register a photo picker activity launcher
     */
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            editCandidateViewModel.candidate.value.photoUri = uri.toString()
            Glide.with(this).load(uri).into(binding.editcandidateImageViewBackground)
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


// [SCROLL,FOCUS CHANGE,CARD ANIMATIONS, ETC... ] --------------------------------

    private var showButtonAnimation: ObjectAnimator? = null
    private var hideButtonAnimation: ObjectAnimator? = null
    private var isButtonVisible = false

    /**
     * Method to check if the save button scrolling anchor is visible on screen
     * @return true if the anchor is visible, false otherwise
     */
    private fun isAnchorViewVisible(): Boolean {
        val rect = Rect()
        binding.editcandidateSaveButtonAnchor.getGlobalVisibleRect(rect)
        val isVisible = rect.bottom > 0 && rect.top < binding.editCandidateScrollview.height
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

    /**
     * Method to zoom the section cards on scroll
     */
    private fun scrollUnZoom(view: View, scrollY: Int, scaling: Float, minUnZoom: Float){
        // Update the scale of the view based on the scroll position
        val newScale = 1f - scrollY / scaling
        // Set a scale limite range
        val currentScale = maxOf(minUnZoom, minOf(1f, newScale))
        view.scaleX = currentScale
        view.scaleY = currentScale
    }

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
                R.id.editname_cardview -> {(3*(view.height/4))}
                //Mail Offset - (this card had the same size of phone card, but the keyboard had an over-bar, we need to adapt the offset at + 3/5 of the card height)
                R.id.editmail_cardview -> {(3*(view.height/5))}
                //Notes Offset - ( + 4/5 of the card height)
                R.id.editnotes_cardview -> { (4*(view.height/5))}
                else -> {0} //Classic card case - (no over-bar on keyboard) - used by : phone / birthday / salary
            }
            //Autoscroll the section card to the good position just over the keyboard
            binding.editCandidateScrollview.scrollTo(0,  (view.top - binding.editCandidateScrollview.height / 2) + offsetDyn)
        }
    }

    /**
     * Methode to change the size of section card with focus change.
     * @param who the card to change the size
     * @param minimized the new "target" size state of the card
     * @param duration the animation duration
     */
    private fun sectionCardSizer(who: MaterialCardView, minimized: Boolean, duration: Long = 300){
        if(minimized){
            who.animate().scaleX(0.94f).scaleY(0.94f).setDuration(duration).start()
        }else{
            who.animate().scaleX(1.0f).scaleY(1.0f).setDuration(duration).start()
        }
    }

    /**
     * Methode to minimize all section cards.
     */
    private fun sectionCardsMinimizer(){
        sectionCardSizer(binding.editnameCardview, true, 0)
        sectionCardSizer(binding.editphoneCardview, true, 0)
        sectionCardSizer(binding.editmailCardview, true, 0)
        sectionCardSizer(binding.editbirthdayCardview, true, 0)
        sectionCardSizer(binding.editsalaryCardview, true, 0)
        sectionCardSizer(binding.editnotesCardview, true, 0)
    }

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