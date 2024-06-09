package com.ocrmission.vitesse.ui.home.candidatesList

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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
     * @param parent The parent view group
     * @param viewType The view type
     * @return A new view holder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CandidateViewHolder{
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_candidate, parent, false)
        return CandidateViewHolder(itemView)
    }

    /**
     * Bind the candidate data to the view holder
     * @param holder The view holder
     * @param position The position of the candidate in the list
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
     * @param newCandidates The new list of candidates
     */
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newCandidates: List<Candidate>) {
        this.candidates = newCandidates

        notifyDataSetChanged()
    }

    /**
     * ViewHolder class for the candidate item
     * @param itemView The view of the candidate item
     * @return A new view holder
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

            // Set click listener on the entire item view
            itemView.setOnClickListener {
                // Handle click event here
                val candidate = candidates[bindingAdapterPosition]

                //todo  remove later by the call to details fragment (HERE JUSTE TO VISUAL DEBUG)
                Toast.makeText(itemView.context, "Candidate clicked: ${candidate.firstname} ${candidate.lastname}", Toast.LENGTH_SHORT).show()

            }


        }
    }



}


