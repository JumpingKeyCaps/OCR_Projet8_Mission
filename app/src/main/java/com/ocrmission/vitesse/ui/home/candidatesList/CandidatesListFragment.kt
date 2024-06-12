package com.ocrmission.vitesse.ui.home.candidatesList

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ocrmission.vitesse.databinding.FragmentCandidatListBinding
import com.ocrmission.vitesse.domain.Candidate
import com.ocrmission.vitesse.ui.home.HomeFragmentDirections
import com.ocrmission.vitesse.ui.home.SharedHomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * The fragment for the list of candidates.
 */
@AndroidEntryPoint
class CandidatesListFragment : Fragment(), OnItemCandidateClickListener {

    private var _binding: FragmentCandidatListBinding? = null
    private val binding get() = _binding!!

    private val candidatesAdapter = CandidatesAdapter(emptyList(),this)

    private var isFirstCollect: Boolean = true


    private val sharedViewModel: SharedHomeViewModel by activityViewModels()





    /**
     * Called when the fragment is first created.
     * @param inflater The layout inflater
     * @param container The view group container
     * @param savedInstanceState The saved instance state bundle.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCandidatListBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Called after the view is created.
     * @param view The view
     * @param savedInstanceState The saved instance state bundle
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //set layout manager to the recyclerview
        binding.candidatesRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        //set the adapter to the recyclerview
        binding.candidatesRecyclerview.adapter = candidatesAdapter

        // setup the observer
        setupObservers()

    }

    /**
     * Setup the observer
     */
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {

            sharedViewModel.candidates.collect { candidates ->
                candidatesAdapter.updateData(candidates)
                emptyCandidateListState(candidates.isEmpty())
                isFirstCollect = false
            }
        }
    }




    /**
     * Method to hide the loading progress indicator.
     */
    private fun hideLoadingProgressIndicator(){
        if(binding.candidatesLoadingProgressIndicator.visibility == View.VISIBLE){
            binding.candidatesLoadingProgressIndicator.visibility = View.GONE
        }
    }

    /**
     * Method to update the UI state on empty candidate list.
     * @param isEmpty True if the list is empty, false otherwise
     */
    private fun emptyCandidateListState(isEmpty: Boolean){
        if(isEmpty){
           if(!isFirstCollect){
               hideLoadingProgressIndicator()
               //hide recyclerview and show empty message
               binding.candidatesEmptyListTextview.visibility = View.VISIBLE
               binding.candidatesRecyclerview.alpha = 0f
           }

        }else{
            //hide the loading progress indicator if visible
            hideLoadingProgressIndicator()
            //hide message and show recyclerview
            binding.candidatesEmptyListTextview.visibility = View.GONE
            binding.candidatesRecyclerview.animate().alpha(1f).duration = 200
        }
    }

    /**
     * Called before fragment is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    /**
     * Method to handle candidate click events and navigate to details candidate screen
     * @param candidate The candidate item to transmit to the details candidate fragment
     */
    override fun onCandidateClicked(candidate: Candidate) {
        //Use the NavController to navigate to the details candidate profile
        val action = HomeFragmentDirections.actionNavigationHomeFragmentToDetailsCandidateFragment(candidate.id)
        findNavController(requireParentFragment().requireView()).navigate(action)


    }

}

/**
 * Interface for handling candidate items click events.
 */
interface OnItemCandidateClickListener {
    fun onCandidateClicked(candidate: Candidate)
}