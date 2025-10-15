package com.example.myjetpack1.repository

import com.example.myjetpack1.db.CurrencyDao
import com.example.myjetpack1.model.Data
import com.example.myjetpack1.network.CurrencyApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyRepository @Inject constructor(
    private val currencyApiService: CurrencyApiService,
    private val currencyDao: CurrencyDao
) {

    fun getCurrency(searchQuery: String, forceRefresh: Boolean): Flow<List<Data>> = flow {
        val cachedCurrency = if (searchQuery.isBlank()) {
            currencyDao.getAllCurrency()
        } else {
            currencyDao.searchCurrency("%$searchQuery%")
        }

        if (cachedCurrency.isNotEmpty() && !forceRefresh) {
            emit(cachedCurrency)
        } else {
            try {
                val remoteCurrency = currencyApiService.getCurrency()
                currencyDao.insertAll(remoteCurrency.data.filter { it.currencyCode == "BTC" || it.currencyCode == "SATS" })
               // currencyDao.insertAll(remoteCurrency.data)
                emit(currencyDao.getAllCurrency())
            } catch (e: Exception) {
                // If API fails, emit cached data if available, otherwise throw error
                if (cachedCurrency.isNotEmpty()) {
                    emit(cachedCurrency)
                } else {
                    throw e
                }
            }
        }
    }
}