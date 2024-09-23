package com.ead.ecommerceapp.data.repository.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("{endpoint}")
    fun getRequest(
        @Path("endpoint") endpoint: String,
        @QueryMap queryParams: Map<String, String>? = null
    ): Call<Any>

    @POST("{endpoint}")
    fun postRequest(
        @Path("endpoint") endpoint: String,
        @Body body: Any
    ): Call<Any>

    @PUT("{endpoint}")
    fun putRequest(
        @Path("endpoint") endpoint: String,
        @Body body: Any
    ): Call<Any>

    @PATCH("{endpoint}")
    fun patchRequest(
        @Path("endpoint") endpoint: String,
        @Body body: Any
    ): Call<Any>

    @DELETE("{endpoint}")
    fun deleteRequest(
        @Path("endpoint") endpoint: String
    ): Call<Any>

    @Multipart
    @POST("{endpoint}")
    fun uploadFile(
        @Path("endpoint") endpoint: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Call<Any>
}