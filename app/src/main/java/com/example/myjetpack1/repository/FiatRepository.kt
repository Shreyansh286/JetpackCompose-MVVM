package com.example.myjetpack1.repository

import com.example.myjetpack1.db.FiatDao
import com.example.myjetpack1.model.Data
import com.example.myjetpack1.network.CurrencyApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FiatRepository @Inject constructor(
    private val currencyApiService: CurrencyApiService,
    private val fiatDao: FiatDao
) {

    fun getFiatCurrency(searchQuery: String, forceRefresh: Boolean): Flow<List<Data>> = flow {
        val cachedCurrency = if (searchQuery.isBlank()) {
            fiatDao.getAllCurrency()
        } else {
            fiatDao.searchCurrency("%$searchQuery%")
        }

        if (cachedCurrency.isNotEmpty() && !forceRefresh) {
            emit(cachedCurrency)
        } else {
            try {
                val remoteCurrency = currencyApiService.getCurrency()
                fiatDao.insertAll(remoteCurrency.data)
                emit(fiatDao.getAllCurrency())
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