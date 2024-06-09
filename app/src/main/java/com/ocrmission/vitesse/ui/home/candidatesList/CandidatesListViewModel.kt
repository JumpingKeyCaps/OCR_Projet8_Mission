package com.ocrmission.vitesse.ui.home.candidatesList

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

/**
 * The ViewModel for the CandidatesList screen.
 */
@HiltViewModel
class CandidatesListViewModel @Inject constructor(
    private val candidateRepository: CandidateRepository,
    private val sharedFilterCandidates: FilterForCandidatesFlow
) : ViewModel() {

    private val _candidates = MutableStateFlow<List<Candidate>>(emptyList())
    val candidates: StateFlow<List<Candidate>> = _candidates.asStateFlow()

    init {
        fetchFilteredCandidates()
    }

    /**
     * Fetches the list of candidates from the repository and apply the shared filter collected.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun fetchFilteredCandidates() {
        viewModelScope.launch(Dispatchers.IO) {
            sharedFilterCandidates.filter.flatMapLatest { filter ->
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




}