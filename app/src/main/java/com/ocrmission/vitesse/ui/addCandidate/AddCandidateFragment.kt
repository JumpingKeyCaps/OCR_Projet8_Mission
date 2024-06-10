package com.ocrmission.vitesse.ui.addCandidate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ocrmission.vitesse.databinding.FragmentAddcandidateBinding
import com.ocrmission.vitesse.domain.Candidate
import dagger.hilt.android.AndroidEntryPoint

/**
 * Add Candidate Fragment
 */
@AndroidEntryPoint
class AddCandidateFragment : Fragment() {
    private lateinit var binding: FragmentAddcandidateBinding
    private var candidate: Candidate? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAddcandidateBinding.inflate(inflater, container, false)

        //get the candidate object in bundle if exist (Edit mode) else if null (Create mode).
        val serializable = arguments?.getSerializable("candidate")
        if (serializable != null){
            candidate = serializable as Candidate
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //todo  remove later ---------------------------------------
        if (candidate != null){
            Toast.makeText(context, "Candidate : ${candidate!!.firstname} ${candidate!!.lastname}", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(context, "Candidate is null, Create Mode !", Toast.LENGTH_SHORT).show()
        }


    }



}