package com.ead.ecommerceapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.json.JSONArray
import org.json.JSONObject

@Parcelize
data class Rating(
    val stars: Int,
    val comment: String,
    val dateCreated: String,
    val customerId: String,
    val isModified: Boolean,
    val dateModified: String?
) : Parcelable {

    companion object {
        // Convert JSONArray to a list of Rating objects
        fun fromJsonArray(jsonArray: JSONArray): List<Rating> {
            val ratings = mutableListOf<Rating>()
            for (i in 0 until jsonArray.length()) {
                val jsonRating: JSONObject = jsonArray.getJSONObject(i)
                ratings.add(
                    Rating(
                        stars = jsonRating.getInt("stars"),
                        comment = jsonRating.getString("comment"),
                        dateCreated = jsonRating.getString("dateCreated"),
                        customerId = jsonRating.getString("customerId"),
                        isModified = jsonRating.getBoolean("isModified"),
                        dateModified = if (jsonRating.isNull("dateModified")) null else jsonRating.getString("dateModified")
                    )
                )
            }
            return ratings
        }
    }
}
