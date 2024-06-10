package com.ocrmission.vitesse.ui.utils

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.ocrmission.vitesse.R
import com.ocrmission.vitesse.domain.Candidate
import com.ocrmission.vitesse.ui.addCandidate.AddCandidateFragment
import com.ocrmission.vitesse.ui.detailsCandidate.DetailsCandidateFragment

/**
 * Navigation utilities object
 */
object NavigationUtils {

    /**
     * Navigate to the details fragment
     * @param fragment The fragment to navigate from.
     * @param candidate The candidate object to transmit via the bundle, if null the "create mode" of details fragment will be triggered.
     */
    fun navigateToDetailsCandidateFragment(fragment: Fragment, candidate: Candidate? = null) {
        val bundle = Bundle()
        bundle.putSerializable("candidate", candidate)

        fragment.parentFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right, R.anim.slide_out_left,
                R.anim.slide_in_left, R.anim.slide_out_right)
            .addToBackStack(null)
            .replace(R.id.fragmentContainerView, DetailsCandidateFragment::class.java, bundle)
            .commit();
    }


    /**
     * Navigate to the add candidate fragment
     * @param fragment The fragment to navigate from.
     * @param candidate The candidate object to transmit via the bundle, if null its a new creation else its an edit
     */
    fun navigateToAddCandidateFragment(fragment: Fragment, candidate: Candidate? = null) {
        val bundle = Bundle()
        bundle.putSerializable("candidate", candidate)

        fragment.parentFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right, R.anim.slide_out_left,
                R.anim.slide_in_left, R.anim.slide_out_right)
            .addToBackStack(null)
            .replace(R.id.fragmentContainerView, AddCandidateFragment::class.java, bundle)
            .commit();
    }


}