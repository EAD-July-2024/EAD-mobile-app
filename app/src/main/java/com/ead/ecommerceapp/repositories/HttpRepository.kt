package com.ead.ecommerceapp.repositories

import android.util.Log
import okhttp3.*
import java.io.File
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

object HttpRepository {
    private const val BASE_URL = "https://ecommerceapp.com/"
    private val client = OkHttpClient()

    // GET Request
    fun getRequest(endpoint: String, callback: (response: String?) -> Unit) {
        val request = Request.Builder()
            .url(BASE_URL + endpoint)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("HttpRepository", "GET request failed", e)
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback(response.body?.string())
                } else {
                    callback(null)
                }
            }
        })
    }

    // POST Request
    fun postRequest(endpoint: String, jsonBody: JSONObject, callback: (response: String?) -> Unit) {
        val body =
            jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val request = Request.Builder()
            .url(BASE_URL + endpoint)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("HttpRepository", "POST request failed", e)
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback(response.body?.string())
                } else {
                    callback(null)
                }
            }
        })
    }

    // PATCH Request
    fun patchRequest(endpoint: String, jsonBody: JSONObject, callback: (response: String?) -> Unit) {
        val body =
            jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val request = Request.Builder()
            .url(BASE_URL + endpoint)
            .patch(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("HttpRepository", "PATCH request failed", e)
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback(response.body?.string())
                } else {
                    callback(null)
                }
            }
        })
    }

    // DELETE Request
    fun deleteRequest(endpoint: String, callback: (response: String?) -> Unit) {
        val request = Request.Builder()
            .url(BASE_URL + endpoint)
            .delete()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("HttpRepository", "DELETE request failed", e)
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback(response.body?.string())
                } else {
                    callback(null)
                }
            }
        })
    }

    // POST Request with file upload
    fun postRequestWithFile(endpoint: String, file: File, callback: (response: String?) -> Unit) {
        val mediaType = "multipart/form-data".toMediaTypeOrNull()
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", file.name, file.asRequestBody(mediaType))
            .build()

        val request = Request.Builder()
            .url(BASE_URL + endpoint)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("HttpRepository", "POST request with file failed", e)
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback(response.body?.string())
                } else {
                    callback(null)
                }
            }
        })
    }
}