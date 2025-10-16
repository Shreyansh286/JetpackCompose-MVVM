package com.example.myjetpack1.repository

import com.example.myjetpack1.db.CountryDao
import com.example.myjetpack1.model.CountryDataItem
import com.example.myjetpack1.network.CountryApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CountryRepository @Inject constructor(
    private val countryApiService: CountryApiService,
    private val countryDao: CountryDao
) {

    fun getCountries(searchQuery: String, forceRefresh: Boolean): Flow<List<CountryDataItem>> =
        flow {
            val cachedCountries = if (searchQuery.isBlank()) {
                countryDao.getAllCountries()
            } else {
                countryDao.searchCountries("%$searchQuery%")
            }

            if (cachedCountries.isNotEmpty() && !forceRefresh) {
                emit(cachedCountries)
            } else {
                try {
                    val remoteCountries = countryApiService.getCountries()
                    countryDao.insertAll(remoteCountries.data)
                    emit(countryDao.getAllCountries())
                } catch (e: Exception) {
                    // If API fails, emit cached data if available, otherwise throw error
                    if (cachedCountries.isNotEmpty()) {
                        emit(cachedCountries)
                    } else {
                        throw e
                    }
                }
            }
        }
}