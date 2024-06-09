package com.ocrmission.vitesse.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ocrmission.vitesse.ui.home.filter.FilterForCandidatesFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
/**
 * The home view model.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val sharedFilterCandidates: FilterForCandidatesFlow
) : ViewModel() {

    /**
     * Method to update the list filter.
     */
    fun updateFilter(filter: String) {
        viewModelScope.launch(Dispatchers.IO) {
            sharedFilterCandidates.updateFilter(filter)
        }
    }

}