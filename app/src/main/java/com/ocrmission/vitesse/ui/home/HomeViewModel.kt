package com.ocrmission.vitesse.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ocrmission.vitesse.domain.Candidate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * The home view model.
 */
@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _candidates = MutableStateFlow<List<Candidate>>(emptyList())
    val candidates: StateFlow<List<Candidate>> = _candidates.asStateFlow()

    fun fetchCandidates() {
        viewModelScope.launch(Dispatchers.IO) {

        }
    }


}