package com.ocrmission.vitesse.ui.home.candidatesList

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.ocrmission.vitesse.R
import com.ocrmission.vitesse.domain.Candidate

/**
 * Adapter for the candidate list
 */
class CandidatesAdapter(private var candidates: List<Candidate>):
    RecyclerView.Adapter<CandidatesAdapter.CandidateViewHolder>() {
    /**
     * Create a new view holder for the candidate item
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CandidateViewHolder{
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_candidate, parent, false)
        return CandidateViewHolder(itemView)
    }

    /**
     * Bind the candidate data to the view holder
     */
    override fun onBindViewHolder(holder: CandidateViewHolder, position: Int) {
            val candidate = candidates[position]
            holder.tvFirstName.text = candidate.firstname
            holder.tvLastName.text = candidate.lastname
            holder.tvNote.text = candidate.note


        // Get the picture with Glide
        val photoUri = candidate.photoUri
        if (photoUri.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(photoUri)
                .transform(CircleCrop())
                .error(R.drawable.default_avatar) // Set a default avatar if loading fails
                .into(holder.ivAvatar) // Load the image into the ImageView
        } else {
            // Handle the case where there is no photo URI
            holder.ivAvatar.setImageResource(R.drawable.default_avatar) // Set a default avatar
        }
    }

    /**
     * Returns the number of candidates
     */
    override fun getItemCount() = candidates.size


    /**
     * Update the list of candidates and notify the adapter
     */
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newCandidates: List<Candidate>) {
        this.candidates = newCandidates

        notifyDataSetChanged()
    }

    /**
     * ViewHolder class for the candidate item
     */
    //ViewHolder
    inner class CandidateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvFirstName: TextView
        var tvLastName: TextView
        var tvNote: TextView
        var ivAvatar: ImageView

        init {
            tvFirstName = itemView.findViewById(R.id.candidate_first_names)
            tvLastName = itemView.findViewById(R.id.candidate_last_names)
            tvNote = itemView.findViewById(R.id.candidate_note)
            ivAvatar = itemView.findViewById(R.id.candidate_photo)


        }
    }



}


