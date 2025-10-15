package com.example.myjetpack1.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.myjetpack1.db.AppDatabase // Import AppDatabase
import com.example.myjetpack1.model.User
import com.example.myjetpack1.network.RandomUserApiService
import com.example.myjetpack1.paging.UserRemoteMediator // Import UserRemoteMediator
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalPagingApi::class) // Required for RemoteMediator
@Singleton
class UserRepository @Inject constructor(
    private val apiService: RandomUserApiService,
    private val appDatabase: AppDatabase // Inject AppDatabase
) {
    fun getUsers(query: String): Flow<PagingData<User>> {
        val userDao = appDatabase.userDao()

        // The PagingSource is now from the local database (UserDao)
        val pagingSourceFactory = { userDao.getUsers(query) }

        return Pager(
            config = PagingConfig(
                pageSize = 20, // Number of items to load per page
                enablePlaceholders = false,
                // initialLoadSize can be configured if needed, defaults to pageSize * 3
            ),
            remoteMediator = UserRemoteMediator(
                apiService = apiService,
                appDatabase = appDatabase,
                query = query // Pass the query to the mediator if it needs to fetch query-specific data
                              // Or, if API doesn't support query for mediator, mediator fetches broadly and DAO filters
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }
}
