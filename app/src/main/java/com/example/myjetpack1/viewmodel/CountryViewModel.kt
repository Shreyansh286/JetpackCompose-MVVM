package com.example.myjetpack1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myjetpack1.model.CountryDataItem
import com.example.myjetpack1.repository.CountryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CountryViewModel @Inject constructor(
    private val countryRepository: CountryRepository
) : ViewModel() {

    private val _countries = MutableStateFlow<List<CountryDataItem>>(emptyList())
    val countries: StateFlow<List<CountryDataItem>> = _countries

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadCountries(searchQuery: String = "", forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            countryRepository.getCountries(searchQuery, forceRefresh)
                .catch { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
                .collectLatest { countryList ->
                    _countries.value = countryList
                    _isLoading.value = false
                }
        }
    }
}