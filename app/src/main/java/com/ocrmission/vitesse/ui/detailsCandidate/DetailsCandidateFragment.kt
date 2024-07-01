package com.ocrmission.vitesse.ui.detailsCandidate

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.ocrmission.vitesse.R
import com.ocrmission.vitesse.databinding.FragmentDetailsCandidateBinding
import com.ocrmission.vitesse.domain.Candidate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


/**
 * Details Candidate Fragment
 */
@AndroidEntryPoint
class DetailsCandidateFragment : Fragment() {

    private lateinit var binding: FragmentDetailsCandidateBinding
    private val args: DetailsCandidateFragmentArgs by navArgs()
    private val detailsCandidateViewModel: DetailsCandidateViewModel by viewModels()


//LIFECYCLE STUFF --------------------------------------------------------------------------

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsCandidateBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //retrieve the candidate id from the arguments
        retrieveCandidateID()
        //observe the candidate flow and the currency rate flow from the viewmodel
        setupObservers()
        //Initialisation the contact buttons
        initContactButtons()
        //Initialisation of the toolbar
        initToolBar()
        //Initialisation of the scrollview change listener
        initScrollChangeListener()
       }


    override fun onResume() {
        super.onResume()
        //Fetch the currency rate UI on resume (auto-refresh the value after internet config change)
        retrieveCurrencyRate()
    }


// [FETCHING DATA] STUFF -----------------------------------------------------------------

    /**
     * Retrieve the candidate id from the navigation arguments
     */
    private fun retrieveCandidateID() {
        val candidateId = args.CandidateId
        detailsCandidateViewModel.fetchingCandidateById(candidateId)
    }

    /**
     * Retrieve the currency rate from the viewmodel
     */
    private fun retrieveCurrencyRate(){
        detailsCandidateViewModel.fetchingCurrencyRate()
        //update the UI (case of internet setting change)
        updateCurrencyRateUI()
    }

    /**
     * Observe the candidate flow and the currency rate flow from the viewmodel
     */
    private fun setupObservers() {
        //observe the candidate flow from the viewmodel
        viewLifecycleOwner.lifecycleScope.launch {
            detailsCandidateViewModel.candidate.collect {
                updateUI(it)
            }
        }
        //observe the currency rate flow from the viewmodel
        viewLifecycleOwner.lifecycleScope.launch {
            detailsCandidateViewModel.currencyRate.collect{
                updateCurrencyRateUI()
            }
        }

        //observe the network error flow from the viewmodel
        viewLifecycleOwner.lifecycleScope.launch {
            detailsCandidateViewModel.networkState.collect{
                //if an network error occurred, show a snackBar with the error message from the collected flow.
                if(it.errorState){
                    Snackbar.make(requireActivity().findViewById(android.R.id.content),it.errorMessage?.let { it1 -> getString(it1) }?:"", Snackbar.LENGTH_LONG).show()
                    //set salary conversion text to no internet message
                    binding.salaryConvertionTextView.text = getString(R.string.details_section_salary_conversion_no_internet)
                }
            }
        }


    }


// [INITIALISATIONS]  --------------------------------------------------------------------

    /**
     * Method to setup the top bar of the screen
     */
    private fun initToolBar(){
        //set listener on the toolbar back arrow button
        binding.detailsToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        //set listener on the toolbar menu buttons
        binding.detailsToolbar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.favmenu -> {
                    // Handle favorite click
                    toggleFavoriteState()
                    true
                }
                R.id.editmenu -> {
                    // Handle edit click
                    //nav to edit screen and pass the CandidateID in argument.
                    findNavController().navigate(
                        DetailsCandidateFragmentDirections.actionNavigationDetailsCandidateFragmentToEditCandidateFragment(detailsCandidateViewModel.candidate.value.id))
                    true
                }
                R.id.deletemenu -> {
                    // Handle delete click
                    showDeleteConfirmationDialog()
                    true
                }
                else -> false
            }
        }
    }

    /**
     * Method to setup the contact buttons (CALL/SMS/MAIL)
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun initContactButtons(){
        //General on touch listener for all contact types buttons
        val masterContactButtonTouchListener = View.OnTouchListener {v, event ->
            when(event.action){
                MotionEvent.ACTION_DOWN -> {
                    v.scaleX = 0.90f
                    v.scaleY = 0.90f
                    return@OnTouchListener true }
                MotionEvent.ACTION_UP -> {
                    v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150).start()
                    when(v.id){
                        binding.smsButton.id -> {
                            sendSMStoCandidate(detailsCandidateViewModel.candidate.value.phone)
                        }
                        binding.mailButton.id -> {
                            sendEmailToCandidate(detailsCandidateViewModel.candidate.value.email)
                        }
                        binding.callButton.id -> {
                            callCandidate(detailsCandidateViewModel.candidate.value.phone)
                        }
                    }
                    return@OnTouchListener true}
                else -> {
                    v.scaleX = 1.0f
                    v.scaleY = 1.0f
                    return@OnTouchListener false
                }
            }
        }
        //call button
        binding.callButton.setOnTouchListener(masterContactButtonTouchListener)
        //sms button
        binding.smsButton.setOnTouchListener(masterContactButtonTouchListener)
        //mail button
        binding.mailButton.setOnTouchListener(masterContactButtonTouchListener)
    }


// [UI UPDATE]  -------------------------------------------------------------------------

    /**
     * Update the UI with the candidate data
     * @param candidate the last updated candidate data
     */
    private fun updateUI(candidate: Candidate) {
        //set the title of toolbar with the candidate name
        binding.detailsToolbar.title = "${candidate.firstname} ${candidate.lastname}"
        //call other ui update methods
        updateCandidatePicture(candidate)
        updateCandidateFavoriteState(candidate)
        updateCandidateDetails(candidate)
    }

    /**
     * Update the currency rate UI with the rate
     */
    @SuppressLint("SetTextI18n")
    private fun updateCurrencyRateUI() {
        val salaryConvertion = Math.round(detailsCandidateViewModel.getSalaryInPound())
        if(detailsCandidateViewModel.getSalaryInPound() == 0.0){
            //salary not set yet  or loading
            binding.salaryConvertionTextView.text = getString(R.string.details_section_salary_conversion_default)
        }else{
            binding.salaryConvertionTextView.text = "${getString(R.string.currency_rate_preword)}  ${getString(R.string.money_symbol_gbp)} $salaryConvertion"
        }


    }

    /**
     * Method to update the candidate picture
     * @param candidate the candidate object
     */
    private fun updateCandidatePicture(candidate: Candidate){
        val uri = Uri.parse(candidate.photoUri)
        Glide.with(requireContext()).load(uri).error(R.drawable.placeholder_pic_a).into(binding.detailsPhotoImageView)
    }

    /**
     * Method to update the candidate details
     * @param candidate the candidate object
     */
    @SuppressLint("SetTextI18n")
    private fun updateCandidateDetails(candidate: Candidate){
        //birthday details
        binding.birthdayDateTextView.text = detailsCandidateViewModel.birthdayDetailsStringBuilder(candidate.birthday)
        binding.birthdayAgeTextView.text = "${detailsCandidateViewModel.birthdayNumberBuilder(candidate.birthday)}"
        //salary details
        if(candidate.salary == 0){
            binding.salaryTextView.text = getString(R.string.no_salary_available)
        }else{
            binding.salaryTextView.text = "${candidate.salary} ${getString(R.string.money_symbol)}"
        }

        //notes details
        if(candidate.note.length<1){
            //No notes for this candidate (notes is not obligatory at creation)
            binding.notesTextView.text = getString(R.string.no_notes_available)
            binding.detailsNotesCard.alpha = 0.3f
        }else{
            //Notes for this candidate
            binding.notesTextView.text = candidate.note
            binding.detailsNotesCard.alpha = 1.0f
        }
        //big firstname and lastname
        binding.detailsBigfirstnameTxtView.text = candidate.firstname
        binding.detailsBignameTxtView.text = candidate.lastname
        //details phone and mail
        binding.detailsPhoneTextView.text = candidate.phone
        binding.detailsMailTextView.text = candidate.email
    }


// [FAVORITES]  --------------------------------------------------------------------------

    /**
     * Method to update the candidate favorite state (toolbar)
     * @param candidate the candidate object
     */
    private fun updateCandidateFavoriteState(candidate: Candidate){
        val isFavorite = candidate.isFavorite // Get the current favorite status (true or false)
        if (isFavorite) {
            binding.detailsToolbar.menu.findItem(R.id.favmenu).icon =  ContextCompat.getDrawable(requireContext(), R.drawable.baseline_star_24)
        } else {
            binding.detailsToolbar.menu.findItem(R.id.favmenu).icon =  ContextCompat.getDrawable(requireContext(), R.drawable.baseline_star_outline_24)
        }
    }

    /**
     * Method to toggle the candidate favorite state
     */
    private fun toggleFavoriteState() {
        if (detailsCandidateViewModel.candidate.value.isFavorite) {
            //candidate is already a favorite, remove it !
            Snackbar.make(requireActivity().findViewById(android.R.id.content),getString(R.string.snack_message_favorite_remove), Snackbar.LENGTH_SHORT).show()
            detailsCandidateViewModel.updateFavoriteState(false)
            binding.detailsToolbar.menu.findItem(R.id.favmenu).icon =  ContextCompat.getDrawable(requireContext(), R.drawable.baseline_star_outline_24)
        }else{
            //candidate is not a favorite, add it !
            Snackbar.make(requireActivity().findViewById(android.R.id.content), getString(R.string.snack_message_favorite_added), Snackbar.LENGTH_SHORT).show()
            detailsCandidateViewModel.updateFavoriteState(true)
            binding.detailsToolbar.menu.findItem(R.id.favmenu).icon =  ContextCompat.getDrawable(requireContext(), R.drawable.baseline_star_24)
        }
    }


// [DELETE CANDIDATE DIALOG]  -------------------------------------------------------------

    /**
     * Method to delete the candidate with confirmation dialog
     */
    private fun showDeleteConfirmationDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setTitle(getString(R.string.delete_candidate_dialog_title))
        builder.setMessage(getString(R.string.delete_candidate_dialog_message))
        builder.setNegativeButton(getString(R.string.delete_candidate_dialog_negative_button)) { dialog, _ ->
            dialog.dismiss() // Dismiss dialog on negative button click
        }
        builder.setPositiveButton(getString(R.string.delete_candidate_dialog_positive_button)) { dialog, _ ->
            // Implement delete logic and navigate to home screen here
            detailsCandidateViewModel.deleteCandidate() // Call function to delete candidate
            dialog.dismiss() // Dismiss dialog on positive button click
            findNavController().navigateUp() // Call function to navigate to home screen
            Snackbar.make(requireActivity().findViewById(android.R.id.content),
                getString(R.string.snack_message_delete_candidate), Snackbar.LENGTH_SHORT).show()
        }
        val dialog = builder.create()
        dialog.show()
    }


// [CONTACTS OPTIONS ACTIONS]  --------------------------------------------------------------------

    /**
     * Method to call the candidate
     * @param candidatePhoneNumber the candidate phone number
     */
    private fun callCandidate(candidatePhoneNumber: String){
        val dialIntent = Intent(Intent.ACTION_DIAL)
        dialIntent.setData(Uri.parse("tel:$candidatePhoneNumber")) // Set phone number
        startActivity(dialIntent) // Start dialer activity
    }

    /**
     * Method to send SMS to the candidate
     * @param candidatePhoneNumber the candidate phone number
     */
    private fun sendSMStoCandidate(candidatePhoneNumber: String){
        val smsIntent = Intent(Intent.ACTION_SENDTO)
        smsIntent.data = Uri.parse("sms:$candidatePhoneNumber")
        startActivity(smsIntent)
    }

    /**
     * Method to send email to the candidate
     * @param candidateEmail the candidate email
     */
    private fun sendEmailToCandidate(candidateEmail: String){
        val emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.data = Uri.parse("mailto:$candidateEmail")
        startActivity(emailIntent)
    }


// [UI/UX]  -------------------------------------------------------------------------

    /**
     * Method to initialise the scrollview change listener - animate some views of the layout
     */
    private fun initScrollChangeListener() {
        //Scroll listener for the nested scrollview
        binding.detailsNestedscrollview.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            //Background Picture translation effect (DECO)
            binding.detailsPhotoImageView.translationY = scrollY/1f
            //BackgroundPicture zoom scaling on scroll (DECO)
            val newScale = (1f + scrollY / 3000f)
            val currentScale = maxOf(1f, minOf(3f, newScale))
            binding.detailsPhotoImageView.scaleX = currentScale
            binding.detailsPhotoImageView.scaleY = currentScale
            // Update the scale of the view based on the scroll position (DECO)
            val newScale2 = 1f - scrollY / 10000f
            // Set a scale limite range
            val currentScale2 = maxOf(0.8f, minOf(1f, newScale2))
            binding.detailsMainresumeCard.scaleX = currentScale2; binding.detailsMainresumeCard.scaleY = currentScale2
            binding.detailsOtherresumeCard.scaleX = currentScale2; binding.detailsOtherresumeCard.scaleY = currentScale2
            binding.detailsContactpannelCard.scaleX = currentScale2; binding.detailsContactpannelCard.scaleY = currentScale2
            binding.detailsSalaryexpectationCard.scaleX = currentScale2; binding.detailsSalaryexpectationCard.scaleY = currentScale2

        }
    }




}