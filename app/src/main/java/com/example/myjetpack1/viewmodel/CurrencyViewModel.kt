package com.example.myjetpack1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myjetpack1.model.Data
import com.example.myjetpack1.repository.CurrencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val currencyRepository: CurrencyRepository
) : ViewModel() {

    private val _currency = MutableStateFlow<List<Data>>(emptyList())
    val currency: StateFlow<List<Data>> = _currency

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadCurrency(searchQuery: String = "", forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            currencyRepository.getCurrency(searchQuery, forceRefresh)
                .catch { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
                .collectLatest { countryList ->
                    _currency.value = countryList
                    _isLoading.value = false
                }
        }
    }
}