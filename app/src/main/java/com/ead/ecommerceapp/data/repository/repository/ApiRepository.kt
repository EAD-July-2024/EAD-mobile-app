package com.ead.ecommerceapp.data.repository.repository

import com.ead.ecommerceapp.data.repository.network.RetrofitClient
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApiRepository {
    private val apiService = RetrofitClient.apiService

    // GET request
    fun get(
        endpoint: String,
        queryParams: Map<String, String>? = null,
        onResponse: (Response<Any>) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        apiService.getRequest(endpoint, queryParams).enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                onResponse(response)
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    // POST request
    fun post(
        endpoint: String,
        body: Any,
        onResponse: (Response<Any>) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        apiService.postRequest(endpoint, body).enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                onResponse(response)
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    // PUT request
    fun put(
        endpoint: String,
        body: Any,
        onResponse: (Response<Any>) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        apiService.putRequest(endpoint, body).enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                onResponse(response)
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    // PATCH request
    fun patch(
        endpoint: String,
        body: Any,
        onResponse: (Response<Any>) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        apiService.patchRequest(endpoint, body).enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                onResponse(response)
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    // DELETE request
    fun delete(
        endpoint: String,
        onResponse: (Response<Any>) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        apiService.deleteRequest(endpoint).enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                onResponse(response)
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    // File upload
    fun uploadFile(
        endpoint: String,
        file: MultipartBody.Part,
        description: RequestBody,
        onResponse: (Response<Any>) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        apiService.uploadFile(endpoint, file, description).enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                onResponse(response)
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                onFailure(t)
            }
        })
    }
}