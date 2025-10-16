package com.example.myjetpack1.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myjetpack1.model.CountryDataItem

@Dao
interface CountryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(countries: List<CountryDataItem>)

    @Query("SELECT * FROM countries")
    suspend fun getAllCountries(): List<CountryDataItem>

    @Query("SELECT * FROM countries WHERE shortName LIKE :searchQuery OR currencyCode LIKE :searchQuery")
    suspend fun searchCountries(searchQuery: String): List<CountryDataItem>

}