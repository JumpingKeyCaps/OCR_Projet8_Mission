package com.ocrmission.vitesse.ui.home.candidatesList

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
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


            if(candidate.note.length>80){
                val truncatedNote = candidate.note.substring(0, 80) + "..."
                holder.tvNote.text = truncatedNote
            }else{
                holder.tvNote.text = candidate.note
            }



            //todo load via glide the picture of user
            //   via candidate.photoUrl
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


