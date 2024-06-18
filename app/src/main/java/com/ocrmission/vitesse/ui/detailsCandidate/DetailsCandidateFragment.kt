package com.ocrmission.vitesse.ui.detailsCandidate

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
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
    private var candidate: Candidate? = null
    private val args: DetailsCandidateFragmentArgs by navArgs()
    private val detailsCandidateViewModel: DetailsCandidateViewModel by viewModels()

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
        //observe the candidate flow from the viewmodel
        setupObservers()
        //setup toolbar
        setupToolBar()

        //setup contact buttons
        setupContactButtons()
    }

    /**
     * Retrieve the candidate id from the navigation arguments
     */
    private fun retrieveCandidateID() {
        val candidateId = args.CandidateId
        detailsCandidateViewModel.fetchingCandidateById(candidateId)
    }

    /**
     * Observe the candidate flow from the viewmodel
     */
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            detailsCandidateViewModel.candidate.collect {
                candidate = it
                if (candidate != null){
                    updateUI(candidate!!)
                    Log.d("favorites", "collect update")
                }
            }
        }
    }

    /**
     * Update the UI with the candidate data
     * @param candidate the last updated candidate data
     */
    private fun updateUI(candidate: Candidate) {
        //set the title of toolbar with the candidate name
        binding.detailsToolbar.title = "${candidate.firstname} ${candidate.lastname}"
        updateCandidatePicture()
        updateCandidateFavoriteState()
        updateCandidateDetails()
    }


    /**
     * Method to update the candidate picture
     */
    private fun updateCandidatePicture(){
        val uri = Uri.parse(candidate?.photoUri)
        Glide.with(requireContext()).load(uri).error(R.drawable.placeholder_pic_a).into(binding.detailsPhotoImageView)
    }


    /**
     * Method to update the candidate details
     */
    private fun updateCandidateDetails(){
        //birthday details
        binding.birthdayDateTextView.text = detailsCandidateViewModel.birthdayDetailsStringBuilder(
            candidate?.birthday,
            getString(R.string.year_word)
        )

        //salary details
        val salaryText = "${candidate?.salary} ${getString(R.string.money_symbol)}"
        binding.salaryTextView.text = salaryText

        //notes details
        binding.notesTextView.text = candidate?.note
    }




    /**
     * Method to update the candidate favorite state (toolbar)
     */
    private fun updateCandidateFavoriteState(){

        val isFavorite = candidate?.isFavorite // Get the current favorite status (true or false)

        if (isFavorite == true) {
            binding.detailsToolbar.menu.findItem(R.id.favmenu).icon =  ContextCompat.getDrawable(requireContext(), R.drawable.baseline_star_24)
        } else {
            binding.detailsToolbar.menu.findItem(R.id.favmenu).icon =  ContextCompat.getDrawable(requireContext(), R.drawable.baseline_star_outline_24)
        }

    }

    /**
     * Method to toggle the candidate favorite state
     */
    private fun toggleFavoriteState() {
        if (candidate?.isFavorite == true) {
            //candidate is already a favorite, remove it !
            Log.d("favorites", "toggleFavoriteState: remove favorite state")

            Snackbar.make(requireActivity().findViewById(android.R.id.content),
                "Removed from favorites !", Snackbar.LENGTH_SHORT).show()

            detailsCandidateViewModel.updateFavoriteState(candidate!!,false)
            binding.detailsToolbar.menu.findItem(R.id.favmenu).icon =  ContextCompat.getDrawable(requireContext(), R.drawable.baseline_star_outline_24)
        }else{
            //candidate is not a favorite, add it !
            Snackbar.make(requireActivity().findViewById(android.R.id.content),
                "Added to favorites !", Snackbar.LENGTH_SHORT).show()

            detailsCandidateViewModel.updateFavoriteState(candidate!!,true)
            binding.detailsToolbar.menu.findItem(R.id.favmenu).icon =  ContextCompat.getDrawable(requireContext(), R.drawable.baseline_star_24)
            Log.d("favorites", "toggleFavoriteState: add favorite state")
        }
    }





    /**
     * Method to setup the top bar
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
                    Toast.makeText(requireContext(), "Edit clicked", Toast.LENGTH_SHORT).show()
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
     * Method to delete the candidate with confirmation dialog
     */
    private fun showDeleteConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Suppression")
        builder.setMessage("Etes-vous sûr de vouloir supprimer ce candidat ? Cette action est irréversible.")
        builder.setNegativeButton("Annuler") { dialog, which ->
            dialog.dismiss() // Dismiss dialog on negative button click
        }
        builder.setPositiveButton("Confirmer") { dialog, which ->
            // Implement delete logic and navigate to home screen here

            dialog.dismiss() // Dismiss dialog on positive button click
            findNavController().navigateUp() // Call function to navigate to home screen

            detailsCandidateViewModel.deleteCandidateById(candidate?.id) // Call function to delete candidate
            //  detailsCandidateViewModel.deleteCandidate(candidate) // Call function to delete candidate

            Snackbar.make(requireActivity().findViewById(android.R.id.content),
                "Candidat supprimer !", Snackbar.LENGTH_SHORT).show()


        }
        val dialog = builder.create()
        dialog.show()
    }


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
                            sendSMStoCandidate(candidate?.phone!!)
                        }
                        binding.mailButton.id -> {
                            sendEmailToCandidate(candidate?.email!!)
                        }
                        binding.callButton.id -> {
                            callCandidate(candidate?.phone!!)
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