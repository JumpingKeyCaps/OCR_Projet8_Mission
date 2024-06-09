package com.ocrmission.vitesse.ui.home.favoritesList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ocrmission.vitesse.data.repository.CandidateRepository
import com.ocrmission.vitesse.domain.Candidate
import com.ocrmission.vitesse.ui.home.filter.FilterForCandidatesFlow
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

@HiltViewModel
class FavoritesListViewModel @Inject constructor(
    private val candidateRepository: CandidateRepository,
    private val sharedFilterCandidates: FilterForCandidatesFlow
) : ViewModel() {

    private val _favCandidates = MutableStateFlow<List<Candidate>>(emptyList())
    val favCandidates: StateFlow<List<Candidate>> = _favCandidates.asStateFlow()


    init {
        fetchFilteredFavoriteCandidates()
    }


    /**
     * Fetch the list of favorite candidates from the repository.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun fetchFilteredFavoriteCandidates() {
        viewModelScope.launch(Dispatchers.IO) {

            sharedFilterCandidates.filter.flatMapLatest { filter ->
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

}