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
        fetchFilteredCandidates()
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
    fun fetchFilteredCandidates() {
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
                //We have our search filtered list of candidates
                _candidates.value = filteredList

                // We just need to apply the favorite filter on it to get the favorites candidate list
                val favoriteCandidates = filteredList.filter { candidate ->
                    candidate.isFavorite
                }
                _favCandidates.value = favoriteCandidates
            }
        }
    }


}