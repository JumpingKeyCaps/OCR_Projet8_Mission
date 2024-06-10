package com.ocrmission.vitesse.ui.home.favoritesList

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ocrmission.vitesse.databinding.FragmentFavoritesListBinding
import com.ocrmission.vitesse.domain.Candidate
import com.ocrmission.vitesse.ui.utils.NavigationUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Fragment to display the list of favorites candidates.
 */
@AndroidEntryPoint
class FavoritesListFragment : Fragment(), OnItemFavoriteClickListener {

    private var _binding: FragmentFavoritesListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FavoritesListViewModel by viewModels()
    private val favoritesAdapter = FavoritesAdapter(emptyList(),this)

    private var isFirstCollect: Boolean = true


    /**
     * Called when the fragment is first created.
     * @param inflater The layout inflater
     * @param container The view group container
     * @param savedInstanceState The saved instance state bundle.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFavoritesListBinding.inflate(inflater, container, false)
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
        binding.favoritesRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        //set the adapter to the recyclerview
        binding.favoritesRecyclerview.adapter = favoritesAdapter
        // setup the observer
        setupObservers()

    }

    /**
     * Setup the observer
     */
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.favCandidates.collect { favorites ->
                favoritesAdapter.updateData(favorites)
                //check if the list is empty, and update the UI accordingly
                emptyFavoriteListState(favorites.isEmpty())
                //flag to keep the loading progress on the 1st collect call, because Synch/Async, 1st call is alway an empty list (finish before the db build)).
                isFirstCollect = false
            }
        }
    }

    /**
     * Method to hide the loading progress indicator.
     */
    fun hideLoadingProgressIndicator(){
        if(binding.favoritesLoadingProgressIndicator.visibility == View.VISIBLE){
            binding.favoritesLoadingProgressIndicator.visibility = View.GONE
        }
    }

    /**
     * Method to update the UI state on empty favorites list.
     * @param isEmpty True if the list is empty, false otherwise
     */
    private fun emptyFavoriteListState(isEmpty: Boolean){
        if(isEmpty){
            if(!isFirstCollect){
                hideLoadingProgressIndicator()
                //hide recyclerview and show empty message
                binding.favoritesEmptyListTextview.visibility = View.VISIBLE
                binding.favoritesRecyclerview.alpha = 0f
            }

        }else{
            //hide the loading progress indicator if visible
            hideLoadingProgressIndicator()
            //hide message and show recyclerview
            binding.favoritesEmptyListTextview.visibility = View.GONE
            binding.favoritesRecyclerview.animate().alpha(1f).duration = 200
        }
    }


    /**
     * Method to handle candidate click events and navigate to candidate Profile
     */
    override fun onFavoriteClicked(favorite: Candidate) {
        // Navigate to details fragment using NavigationUtils object
        parentFragment?.let { NavigationUtils.navigateToDetailsCandidateFragment(it, favorite) }

    }

    /**
     * Called before fragment is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
/**
 * Interface for handling favorite items click events.
 */
interface OnItemFavoriteClickListener {
    fun onFavoriteClicked(favorite: Candidate)
}