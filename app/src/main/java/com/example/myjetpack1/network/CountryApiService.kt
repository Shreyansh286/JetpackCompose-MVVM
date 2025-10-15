package com.example.myjetpack1.network

import com.example.myjetpack1.model.CountryResponse
import com.example.myjetpack1.model.CurrencyResponse
import retrofit2.http.GET

interface CountryApiService {
    @GET("jayminsoni-speed/android-practical/main/country_data.json")
    suspend fun getCountries(): CountryResponse
}