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

        //retrieve the currency rate
        retrieveCurrencyRate()

        //observe the candidate flow from the viewmodel
        setupObservers()
        //setup the contact buttons
        setupContactButtons()
        //setup the toolbar
        setupToolBar()
       }


//SETUP STUFF --------------------------------------------------------------------------
    /**
     * Retrieve the candidate id from the navigation arguments
     */
    private fun retrieveCandidateID() {
        val candidateId = args.CandidateId
        detailsCandidateViewModel.fetchingCandidateById(candidateId)
    }

    private fun retrieveCurrencyRate(){
        detailsCandidateViewModel.getCurrencyRate("eur","gbp")
    }



    /**
     * Observe the candidate flow from the viewmodel
     */
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            detailsCandidateViewModel.candidate.collect {
                updateUI(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            detailsCandidateViewModel.currencyRate.collect{
                updateCurrencyRateUI(it)
            }
        }

    }



    /**
     * Method to setup the top bar of the screen
     */
    private fun setupToolBar(){

        //set all listener on the toolbar buttons
        binding.detailsToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

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


//UPDATE STUFF --------------------------------------------------------------------------

    /**
     * Update the UI with the candidate data
     * @param candidate the last updated candidate data
     */
    private fun updateUI(candidate: Candidate) {
        //set the title of toolbar with the candidate name
        binding.detailsToolbar.title = "${candidate.firstname} ${candidate.lastname}"
        updateCandidatePicture(candidate)
        updateCandidateFavoriteState(candidate)
        updateCandidateDetails(candidate)
    }


    @SuppressLint("SetTextI18n")
    private fun updateCurrencyRateUI(rate: Double) {

        val salaryConvertion = Math.round(detailsCandidateViewModel.candidate.value.salary * rate)

        binding.salaryConvertionTextView.text = "${getString(R.string.currency_rate_preword)}  ${getString(R.string.money_symbol_gbp)} $salaryConvertion"
    }




    /**
     * Method to update the candidate picture
     */
    private fun updateCandidatePicture(candidate: Candidate){
        val uri = Uri.parse(candidate.photoUri)
        Glide.with(requireContext()).load(uri).error(R.drawable.placeholder_pic_a).into(binding.detailsPhotoImageView)
    }


    /**
     * Method to update the candidate details
     */
    private fun updateCandidateDetails(candidate: Candidate){
        //birthday details
        binding.birthdayDateTextView.text = detailsCandidateViewModel.birthdayDetailsStringBuilder(
            candidate.birthday,
            getString(R.string.year_word)
        )

        //salary details
        val salaryText = "${candidate.salary} ${getString(R.string.money_symbol)}"
        binding.salaryTextView.text = salaryText


        //notes details
        binding.notesTextView.text = candidate.note
    }




//FAVORITES STUFF --------------------------------------------------------------------------

    /**
     * Method to update the candidate favorite state (toolbar)
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

            Snackbar.make(requireActivity().findViewById(android.R.id.content),
                "Removed from favorites !", Snackbar.LENGTH_SHORT).show()

            detailsCandidateViewModel.updateFavoriteState(false)
            binding.detailsToolbar.menu.findItem(R.id.favmenu).icon =  ContextCompat.getDrawable(requireContext(), R.drawable.baseline_star_outline_24)
        }else{
            //candidate is not a favorite, add it !
            Snackbar.make(requireActivity().findViewById(android.R.id.content),
                "Added to favorites !", Snackbar.LENGTH_SHORT).show()

            detailsCandidateViewModel.updateFavoriteState(true)
            binding.detailsToolbar.menu.findItem(R.id.favmenu).icon =  ContextCompat.getDrawable(requireContext(), R.drawable.baseline_star_24)
        }
    }






//DELETE STUFF --------------------------------------------------------------------------

    /**
     * Method to delete the candidate with confirmation dialog
     */
    private fun showDeleteConfirmationDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setTitle("Suppression")
        builder.setMessage("Etes-vous sûr de vouloir supprimer ce candidat ? Cette action est irréversible.")
        builder.setNegativeButton("Annuler") { dialog, _ ->
            dialog.dismiss() // Dismiss dialog on negative button click
        }
        builder.setPositiveButton("Confirmer") { dialog, _ ->
            // Implement delete logic and navigate to home screen here
            detailsCandidateViewModel.deleteCandidate() // Call function to delete candidate
            dialog.dismiss() // Dismiss dialog on positive button click
            findNavController().navigateUp() // Call function to navigate to home screen
            Snackbar.make(requireActivity().findViewById(android.R.id.content),
                "Candidat supprimer !", Snackbar.LENGTH_SHORT).show()
        }
        val dialog = builder.create()
        dialog.show()
    }


//CONTACTS STUFF --------------------------------------------------------------------------


    /**
     * Method to setup the contact buttons (CALL/SMS/MAIL)
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun setupContactButtons(){
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

    //call candidate
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



}