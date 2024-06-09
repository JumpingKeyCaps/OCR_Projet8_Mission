package com.ocrmission.vitesse.ui.home.filter

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Shared Filter flow for candidates list.
 * (from the HomeViewModel to CandidatesListFlow and FavoritesListFlow)
 */
object FilterForCandidatesFlow {
    private val _filter = MutableStateFlow("")
    val filter: StateFlow<String> = _filter.asStateFlow()

    /**
     * Update the filter value.
     * @param newFilter the new filter value.
     */
    fun updateFilter(newFilter: String) {
        _filter.value = newFilter
    }
}