package com.ead.ecommerceapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ead.ecommerceapp.adapters.RatingAdapter
import com.ead.ecommerceapp.databinding.ActivityVendorDetailBinding
import com.ead.ecommerceapp.databinding.DialogEditCommentBinding
import com.ead.ecommerceapp.databinding.DialogRatingBinding
import com.ead.ecommerceapp.models.Rating
import com.ead.ecommerceapp.repositories.HttpRepository
import com.ead.ecommerceapp.utils.SessionManager
import org.json.JSONObject

class VendorDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVendorDetailBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var ratingAdapter: RatingAdapter
    private var vendorId: String? = null
    private var ratingList: List<Rating> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVendorDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        vendorId = intent.getStringExtra("vendorId")
        val currentUserId = sessionManager.getUserId()

        fetchVendorDetails()

        // Set up rating adapter
        ratingAdapter = RatingAdapter(this, ratingList, currentUserId ?: "") { rating ->
            if (sessionManager.getToken() != null) {
                showEditCommentDialog(rating)
            } else {
                promptLogin()
            }
        }
        binding.recyclerViewRatings.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewRatings.adapter = ratingAdapter

        // Add rating button click
        binding.addRatingButton.setOnClickListener {
            if (sessionManager.getToken() != null) {
                showRatingDialog()
            } else {
                promptLogin()
            }
        }
    }

    private fun fetchVendorDetails() {
        vendorId?.let {
            HttpRepository.getRequest("vendor/vendorSingle/$it") { response ->
                if (response != null) {
                    val vendorJson = JSONObject(response)
                    runOnUiThread {
                        binding.vendorNameDetail.text = vendorJson.getString("fullName")
                        binding.vendorContactDetail.text = vendorJson.getString("contactInfo")
                        binding.vendorRatingDetail.text = vendorJson.getString("averageRating")
                        val ratings = vendorJson.getJSONArray("ratings")
                        ratingList = Rating.fromJsonArray(ratings)
                        ratingAdapter.updateRatings(ratingList)
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "Failed to fetch vendor details", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    // Show a dialog to add a new rating
    private fun showRatingDialog() {
        val dialogBinding = DialogRatingBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogBinding.root)
            .setTitle("Add Rating")
            .setPositiveButton("Submit") { _, _ ->
                val stars = dialogBinding.ratingBar.rating.toInt()
                val comment = dialogBinding.commentText.text.toString()
                if (stars > 0 && comment.isNotEmpty()) {
                    submitRating(stars, comment)
                } else {
                    Toast.makeText(this, "Please provide a rating and comment", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // POST request to add a new rating
    private fun submitRating(stars: Int, comment: String) {
        val customerId = sessionManager.getUserId()
        val token = sessionManager.getToken()

        if (token == null) {
            promptLogin() // Redirect to login if not logged in
            return
        }

        if (customerId != null && vendorId != null) {
            val jsonBody = JSONObject().apply {
                put("customerId", customerId)
                put("vendorId", vendorId)
                put("stars", stars)
                put("comment", comment)
            }

            HttpRepository.postRequestWithToken("rating", jsonBody, token) { response ->
                if (response != null) {
                    runOnUiThread {
                        Toast.makeText(this, "Rating submitted", Toast.LENGTH_SHORT).show()
                        fetchVendorDetails() // Refresh the ratings
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "Failed to submit rating", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    // Show a dialog to edit an existing comment
    private fun showEditCommentDialog(rating: Rating) {
        val dialogBinding = DialogEditCommentBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogBinding.root)
            .setTitle("Edit Comment")
            .setPositiveButton("Update") { _, _ ->
                val newComment = dialogBinding.editCommentText.text.toString()
                if (newComment.isNotEmpty()) {
                    updateComment(rating, newComment)
                } else {
                    Toast.makeText(this, "Comment cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()

        // Pre-fill the existing comment in the dialog
        dialogBinding.editCommentText.setText(rating.comment)
    }

    // PUT request to update the comment
    private fun updateComment(rating: Rating, newComment: String) {
        val token = sessionManager.getToken()

        if (token == null) {
            promptLogin() // Redirect to login if not logged in
            return
        }

        val jsonBody = JSONObject().apply {
            put("customerId", sessionManager.getUserId())
            put("vendorId", vendorId)
            put("newComment", newComment)
        }

        HttpRepository.putRequestWithToken("rating/update-comment", jsonBody, token) { response ->
            if (response != null) {
                runOnUiThread {
                    Toast.makeText(this, "Comment updated", Toast.LENGTH_SHORT).show()
                    fetchVendorDetails() // Refresh the ratings
                }
            } else {
                runOnUiThread {
                    Toast.makeText(this, "Failed to update comment", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun promptLogin() {
        Toast.makeText(this, "You need to log in to perform this action", Toast.LENGTH_SHORT).show()
        // Redirect to Login Activity
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Optionally, you can finish the current activity so the user cannot navigate back without logging in
    }

}
