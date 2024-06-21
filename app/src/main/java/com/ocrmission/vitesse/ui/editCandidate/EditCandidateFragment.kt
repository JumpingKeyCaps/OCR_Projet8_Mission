package com.ocrmission.vitesse.ui.editCandidate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.ocrmission.vitesse.R
import com.ocrmission.vitesse.databinding.FragmentEditCandidateBinding
import com.ocrmission.vitesse.ui.detailsCandidate.DetailsCandidateFragmentArgs
import dagger.hilt.android.AndroidEntryPoint

/**
 * Edit Candidate Fragment
 */
@AndroidEntryPoint
class EditCandidateFragment : Fragment() {
    private lateinit var binding: FragmentEditCandidateBinding
 //   private val viewModel: EditCandidateViewModel by viewModels()

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

    }


    /**
     * Retrieve the candidate id from the navigation arguments
     */
    private fun retrieveCandidateID() {
        val candidateId = args.CandidateId
        Toast.makeText(requireContext(), "Edit Candidate with id:  $candidateId", Toast.LENGTH_SHORT).show()

    }



}