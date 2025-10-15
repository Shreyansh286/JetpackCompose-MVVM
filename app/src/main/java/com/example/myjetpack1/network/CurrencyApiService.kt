package com.example.myjetpack1.network

import com.example.myjetpack1.model.CurrencyResponse
import retrofit2.http.GET

interface CurrencyApiService {
    @GET("jayminsoni-speed/android-practical/main/currency_data.json")
    suspend fun getCurrency(): CurrencyResponse
}