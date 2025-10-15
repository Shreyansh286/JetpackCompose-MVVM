package com.example.myjetpack1.network

import com.example.myjetpack1.model.UserResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RandomUserApiService {
    @GET("api/")
    suspend fun getUsers(
        @Query("page") page: Int,
        @Query("results") results: Int = 20,
        @Query("seed") seed: String = "abc"
    ): UserResponse
}