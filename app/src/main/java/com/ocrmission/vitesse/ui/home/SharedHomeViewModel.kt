package com.ocrmission.vitesse.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ocrmission.vitesse.data.repository.CandidateRepository
import com.ocrmission.vitesse.domain.Candidate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * The Shared ViewModel between home fragment
 * and CandidateList fragment / FavoritesList fragment
 */
@HiltViewModel
class SharedHomeViewModel @Inject constructor(
    private val candidateRepository: CandidateRepository
) : ViewModel() {


    //FILTER STUFF
    private val _filter = MutableStateFlow("")
    val filter: StateFlow<String> = _filter.asStateFlow()
    //CANDIDATES LIST STUFF
    private val _candidates = MutableStateFlow<List<Candidate>>(emptyList())
    val candidates: StateFlow<List<Candidate>> = _candidates.asStateFlow()
    //FAVORITES LIST STUFF
    private val _favCandidates = MutableStateFlow<List<Candidate>>(emptyList())
    val favCandidates: StateFlow<List<Candidate>> = _favCandidates.asStateFlow()



    init {
      //  fetchFilteredCandidates()
      //  fetchFilteredFavoriteCandidates()

        fetchCandidates(false)
        fetchCandidates(true)


    }

    /**
     * Update the filter value.
     * @param newFilter the new filter value.
     */
    fun updateFilter(newFilter: String) {
        _filter.value = newFilter
    }





    /**
     * Fetches the list of candidates from the repository and apply the shared filter collected.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun fetchFilteredCandidates() {
        viewModelScope.launch(Dispatchers.IO) {
            filter.flatMapLatest { filter ->
                candidateRepository.getAllCandidates().map { candidatesDtos ->
                    candidatesDtos.map { Candidate.fromDto(it) }
                        .filter { candidate ->
                            val searchString = filter.lowercase() // Convert filter to lowercase for case-insensitive matching
                            (candidate.firstname.lowercase().startsWith(searchString) || candidate.lastname.lowercase().startsWith(searchString)
                                    || (candidate.firstname.lowercase() + " " + candidate.lastname.lowercase()).startsWith(searchString)
                                    || (candidate.lastname.lowercase() + " " + candidate.firstname.lowercase()).startsWith(searchString)
                                    )
                        }
                }
            }.collect { filteredList ->
                _candidates.value = filteredList
            }
        }
    }

    /**
     * Fetch the list of favorite candidates from the repository.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun fetchFilteredFavoriteCandidates() {
        viewModelScope.launch(Dispatchers.IO) {

            filter.flatMapLatest { filter ->
                candidateRepository.getAllCandidates().map { candidatesDtos ->
                    candidatesDtos.map { Candidate.fromDto(it) }
                        .filter { candidate ->
                            val searchString = filter.lowercase() // Convert filter to lowercase for case-insensitive matching
                            (candidate.isFavorite && (candidate.firstname.lowercase().startsWith(searchString) || candidate.lastname.lowercase().startsWith(searchString)
                                    || (candidate.firstname.lowercase() + " " + candidate.lastname.lowercase()).startsWith(searchString)
                                    || (candidate.lastname.lowercase() + " " + candidate.firstname.lowercase()).startsWith(searchString)
                                    ))// filter by favorite
                        }
                }
            }.collect { filteredList ->
                _favCandidates.value = filteredList
            }

        }
    }

    /**
     * Fetch the list of candidates OR favorite  from the repository with the filter.
     * @param modeFavorite if true, fetch favorite candidates, otherwise fetch all candidates.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun fetchCandidates(modeFavorite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {

            filter.flatMapLatest { filter ->
                candidateRepository.getAllCandidates().map { candidatesDtos ->
                    candidatesDtos.map { Candidate.fromDto(it) }
                        .filter { candidate ->
                            //favorite filter
                            if(modeFavorite){
                                val searchString = filter.lowercase() // Convert filter to lowercase for case-insensitive matching
                                (candidate.isFavorite && (candidate.firstname.lowercase().startsWith(searchString) || candidate.lastname.lowercase().startsWith(searchString)
                                        || (candidate.firstname.lowercase() + " " + candidate.lastname.lowercase()).startsWith(searchString)
                                        || (candidate.lastname.lowercase() + " " + candidate.firstname.lowercase()).startsWith(searchString)
                                        ))// filter by favorite
                            }else{
                                //classic filter
                                val searchString = filter.lowercase()
                                (candidate.firstname.lowercase().startsWith(searchString) || candidate.lastname.lowercase().startsWith(searchString)
                                        || (candidate.firstname.lowercase() + " " + candidate.lastname.lowercase()).startsWith(searchString)
                                        || (candidate.lastname.lowercase() + " " + candidate.firstname.lowercase()).startsWith(searchString)
                                        )
                            }
                        }
                }
            }.collect { filteredList ->
                if(modeFavorite){
                    _favCandidates.value = filteredList
                }else{
                    _candidates.value = filteredList
                }

            }

        }
    }




}