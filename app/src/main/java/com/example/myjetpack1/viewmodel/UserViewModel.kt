package com.example.myjetpack1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.myjetpack1.model.User
import com.example.myjetpack1.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val users: Flow<PagingData<User>> = _searchQuery
        .flatMapLatest { query ->
            userRepository.getUsers(query)
        }
        .cachedIn(viewModelScope)

    fun searchUsers(query: String) {
        _searchQuery.value = query
    }
}
