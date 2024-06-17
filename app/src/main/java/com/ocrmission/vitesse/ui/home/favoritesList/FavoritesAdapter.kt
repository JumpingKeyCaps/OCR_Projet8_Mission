package com.ocrmission.vitesse.ui.home.favoritesList

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
 * Adapter for the favorites list
 */
class FavoritesAdapter(private var favorites: List<Candidate>,private val onItemClickListener: OnItemFavoriteClickListener):
    RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder>() {
    /**
     * Create a new view holder for the favorites item
     * @param parent The parent view group
     * @param viewType The view type
     * @return A new view holder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesAdapter.FavoriteViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_favorite, parent, false)
        return FavoriteViewHolder(itemView)
    }

    /**
     * Bind the candidate data to the view holder
     * @param holder The view holder
     * @param position The position of the favorite in the list
     */
    override fun onBindViewHolder(holder: FavoritesAdapter.FavoriteViewHolder, position: Int) {
        val candidate = favorites[position]
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
     * Returns the number of favorites in the list
     */
    override fun getItemCount() = favorites.size


    /**
     * Update the list of favorites and notify the adapter
     * @param newFavorites The new list of favorites
     */
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newFavorites: List<Candidate>) {
        this.favorites = newFavorites.reversed()
        notifyDataSetChanged()
    }


    /**
     * ViewHolder class for the favorites item
     * @param itemView The view of the favorites item
     * @return A new view holder
     */
    //ViewHolder
    inner class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvFirstName: TextView
        var tvLastName: TextView
        var tvNote: TextView
        var ivAvatar: ImageView

        init {
            tvFirstName = itemView.findViewById(R.id.favorite_first_names)
            tvLastName = itemView.findViewById(R.id.favorite_last_names)
            tvNote = itemView.findViewById(R.id.favorite_note)
            ivAvatar = itemView.findViewById(R.id.favorite_photo)

            // Set click listener on the entire item view
            itemView.setOnClickListener {
                // Handle click event here
                val candidate = favorites[bindingAdapterPosition]

                //nav to details fragment with this candidate
                onItemClickListener.onFavoriteClicked(candidate)



            }


        }
    }




}