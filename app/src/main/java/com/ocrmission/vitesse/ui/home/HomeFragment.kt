package com.ocrmission.vitesse.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.ocrmission.vitesse.R
import com.ocrmission.vitesse.databinding.FragmentHomeBinding
import com.ocrmission.vitesse.ui.home.candidatesList.CandidatesListFragment
import com.ocrmission.vitesse.ui.home.favoritesList.FavoritesListFragment
import com.ocrmission.vitesse.ui.utils.NavigationUtils
import dagger.hilt.android.AndroidEntryPoint

/**
 * The Home Fragment.
 */
@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()

    /**
     * Fragment LifeCycle - called when the fragment is create.
     */
    override fun onCreateView( inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        //Setup the UI
        SetupUI()
        return binding.root
    }

    /**
     * Fragment LifeCycle - called when the fragment was created.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    /**
     * Initialize the UI.
     */
    private fun SetupUI(){
        //init ViewPager
        setupViewPager()
        //init TabLayout
        setupTabLayout()
        //init SearchBar
        setupSearchBar()
        //init FAB
        setupFAB()

    }


    /**
     * Initialize the view pager.
     */
    private fun setupViewPager(){
        // Create an instance of FragmentStateAdapter with 2 pages
        val adapter = object : FragmentStateAdapter(childFragmentManager,viewLifecycleOwner.lifecycle) {
            override fun getItemCount(): Int {
                return 2 // Nombre de fragments à afficher
            }
            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> CandidatesListFragment() // Fragment for the first page : Candidates List
                    1 -> FavoritesListFragment() // Fragment for the second page : Favorite List
                    else -> throw IllegalArgumentException("Invalid fragment position")
                }
            }
        }
        // Set the adapter for the ViewPager
        binding.homeViewPager.adapter = adapter
    }

    /**
     * Initialize the tab layout.
     */
    private fun setupTabLayout(){
        // Connect TabLayout with ViewPager using TabLayoutMediator
        val tabLayoutMediator = TabLayoutMediator(binding.tabs, binding.homeViewPager) { tab, position ->
            // Set tab text or icon based on position
            when (position) {
                0 -> { tab.text = getString(R.string.tab_AllCandidat_text)}
                1 -> { tab.text = getString(R.string.tab_FavCandidat_text)}
            }
        }
        // Attach TabLayoutMediator to the ViewPager
        tabLayoutMediator.attach()
    }


    /**
     * initialize the searchbar
     *
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun setupSearchBar(){
        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Code à exécuter pendant que le texte est modifié

            }

            override fun afterTextChanged(s: Editable?) {

                //change the search icon to a cross to fast reset search.
                if(s.toString().isEmpty()){
                    binding.searchIcon.setImageResource(R.drawable.baseline_search_a)
                }else{
                    binding.searchIcon.setImageResource(R.drawable.erase_search_b)
                }



                // Code à exécuter après que le texte a été modifié
                val searchQuery = s.toString()
                Log.d("HomeFragment", "Search query: $searchQuery")
                // Utilisez searchQuery pour effectuer une recherche, mettre à jour l'interface utilisateur, etc.
                viewModel.updateFilter(searchQuery)
            }
        })




        //search reset icon listener
        binding.searchIcon.setOnTouchListener{v, event ->
            when(event.action){
                MotionEvent.ACTION_DOWN -> {
                    v.animate().cancel()
                    v.scaleX = 0.8f
                    v.scaleY = 0.8f
                    return@setOnTouchListener true
                }
                MotionEvent.ACTION_UP -> {
                    v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
                    if(binding.searchInput.text.toString().isNotEmpty()){
                        binding.searchInput.text.clear()
                    }
                    return@setOnTouchListener true
                }
                else -> {
                    v.scaleX = 1.0f
                    v.scaleY = 1.0f
                    return@setOnTouchListener false
                }
            }
        }


    }

    /**
     * Initialize the FAB.
     */
    fun setupFAB(){
        binding.fab.setOnClickListener {
            navigateToDetailFragment()
        }
    }




    /**
     * Navigate to the Create fragment, to create a new candidate.
     */
    private fun navigateToDetailFragment() {
        NavigationUtils.navigateToAddCandidateFragment(this)
    }


}