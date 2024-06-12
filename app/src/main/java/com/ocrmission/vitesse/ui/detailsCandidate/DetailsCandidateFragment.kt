package com.ocrmission.vitesse.ui.detailsCandidate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.ocrmission.vitesse.databinding.FragmentDetailsCandidateBinding
import com.ocrmission.vitesse.domain.Candidate
import dagger.hilt.android.AndroidEntryPoint


/**
 * Details Candidate Fragment
 */
@AndroidEntryPoint
class DetailsCandidateFragment : Fragment() {

    private lateinit var binding: FragmentDetailsCandidateBinding
    private var candidate: Candidate? = null
    private val args: DetailsCandidateFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentDetailsCandidateBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //retrieve the candidate id from the arguments
        val candidateId = args.CandidateId

        Toast.makeText(context, "Candidate ID : $candidateId", Toast.LENGTH_SHORT).show()


    }

}