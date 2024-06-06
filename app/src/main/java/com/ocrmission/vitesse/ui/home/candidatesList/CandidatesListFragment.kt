package com.ocrmission.vitesse.ui.home.candidatesList

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.ocrmission.vitesse.R
import com.ocrmission.vitesse.databinding.FragmentCandidatListBinding
import com.ocrmission.vitesse.domain.Candidate
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime

@AndroidEntryPoint
class CandidatesListFragment : Fragment() {

    private var _binding: FragmentCandidatListBinding? = null
    private val binding get() = _binding!!


    //todo: put the viewmodel init there !


    //the adapter
    private val candidatesAdapter = CandidatesAdapter(emptyList())


    /**
     * Called when the fragment is first created.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCandidatListBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //set layout manager to the recyclerview
        binding.candidatesRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        //set the adapter to the recyclerview
        binding.candidatesRecyclerview.adapter = candidatesAdapter

        //DEV ONLY

        //add fake list to test rendering
        candidatesAdapter.updateData(giveMeFakeList())

    }

    /**
     * Called before fragment is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    //DEV ONLY -------------------
    fun giveMeFakeList(): List<Candidate> {
        val fakeList = listOf(
            Candidate(1,"Thomas","Doe","Jdoe@gmail.com","0664655434",LocalDateTime.now(),1500L,"Praesent quis libero quis odio hendrerit tincidunt sit amet et elit. Aliquam suscipit enim nulla, id bibendum sapien dapibus eu. Integer eu fermentum quam. Integer eu mauris ex.","http//pic.com",isFavorite = false),
            Candidate(2,"Mickeal","Clarke","MClark@gmail.com","066423452334",LocalDateTime.now(),2500L,"Praesent quis libero quis odio hendrerit tincidunt sit amet et elit. Aliquam suscipit enim nulla, id bibendum sapien dapibus eu. Integer eu fermentum quam. Integer eu mauris ex.","http//pic.com",isFavorite = false),
            Candidate(3, "Emma", "Watson", "emma.watson@example.com", "0612345678", LocalDateTime.now().minusDays(5), 2000, "Passionate about Android development and eager to learn.", "https://example.com/emma.jpg",isFavorite = false),
            Candidate(4, "Daniel", "Radcliffe", "daniel.radcliffe@example.com", "0687654321", LocalDateTime.now().minusDays(10), 1800, "Experienced in Java and Kotlin, looking for a challenging role.", "https://example.com/daniel.jpg",isFavorite = false),
            Candidate(5, "Rupert", "Grint", "rupert.grint@example.com", "0698765432", LocalDateTime.now().minusDays(3), 2200, "Strong understanding of software design patterns and best practices.", "https://example.com/rupert.jpg",isFavorite = false),
            Candidate(6, "Olivia", "Wilde", "olivia.wilde@example.com", "0654321098", LocalDateTime.now().minusDays(7), 2100, "Proficient in UI/UX design and mobile app development.", "https://example.com/olivia.jpg",isFavorite = false),
            Candidate(7, "Chris", "Evans", "chris.evans@example.com", "0676543210", LocalDateTime.now().minusDays(1), 2300, "Dedicated and results-oriented, with a focus on clean code.", "https://example.com/chris.jpg",isFavorite = false),
            Candidate(8, "Scarlett", "Johansson", "scarlett.johansson@example.com", "0609876543", LocalDateTime.now().minusDays(12), 2400, "Proven ability to work independently and as part of a team.", "https://example.com/scarlett.jpg",isFavorite = false),
            Candidate(9, "Robert", "Downey Jr.", "robert.downeyjr@example.com", "0623456789", LocalDateTime.now().minusDays(9), 2600, "Highly motivated and passionate about creating innovative mobile experiences.", "https://example.com/robert.jpg",isFavorite = false),
            Candidate(10, "Mark", "Ruffalo", "mark.ruffalo@example.com", "0634567890", LocalDateTime.now().minusDays(4), 1900, "Strong problem-solving skills and a commitment to continuous improvement.", "https://example.com/mark.jpg",isFavorite = false)
        )

        return fakeList
    }



}