package com.ead.ecommerceapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ead.ecommerceapp.R
import com.ead.ecommerceapp.databinding.ItemRatingBinding
import com.ead.ecommerceapp.models.Rating

class RatingAdapter(
    private val context: Context,
    private var ratings: List<Rating>,
    private val currentUserId: String, // Pass the logged-in userId
    private val onEditClick: (Rating) -> Unit // Callback for edit click
) : RecyclerView.Adapter<RatingAdapter.RatingViewHolder>() {

    // ViewHolder for Rating item
    class RatingViewHolder(val binding: ItemRatingBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingViewHolder {
        val binding = ItemRatingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RatingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RatingViewHolder, position: Int) {
        val rating = ratings[position]
        holder.binding.ratingStars.text = "Stars: ${rating.stars}"
        holder.binding.ratingComment.text = rating.comment
        holder.binding.ratingDate.text = "Created on: ${rating.dateCreated}"

        // Show "Edit" option only for the logged-in user's comment
        if (rating.customerId == currentUserId) {
            holder.binding.editRatingButton.visibility = View.VISIBLE
            holder.binding.editRatingButton.setOnClickListener {
                onEditClick(rating) // Trigger callback when edit button is clicked
            }
        } else {
            holder.binding.editRatingButton.visibility = View.GONE
        }

        // If rating was modified, show the modified date
        if (rating.isModified) {
            holder.binding.ratingModifiedDate.visibility = View.VISIBLE
            holder.binding.ratingModifiedDate.text = "Modified on: ${rating.dateModified}"
        } else {
            holder.binding.ratingModifiedDate.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return ratings.size
    }

    // Update the list of ratings and notify the adapter
    fun updateRatings(newRatings: List<Rating>) {
        this.ratings = newRatings
        notifyDataSetChanged()
    }
}
