package com.example.myjetpack1.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myjetpack1.model.CountryDataItem
import com.example.myjetpack1.model.CurrencyResponse
import com.example.myjetpack1.model.Data

@Dao
interface FiatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(currency: List<Data>)

    @Query("SELECT * FROM currency")
    suspend fun getAllCurrency(): List<Data>

    @Query("SELECT * FROM currency WHERE currencyName LIKE :searchCurrency OR currencyCode LIKE :searchCurrency")
    suspend fun searchCurrency(searchCurrency: String): List<Data>
}