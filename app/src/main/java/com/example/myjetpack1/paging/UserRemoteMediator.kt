package com.example.myjetpack1.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.myjetpack1.db.AppDatabase
import com.example.myjetpack1.db.RemoteKeys
import com.example.myjetpack1.model.User
import com.example.myjetpack1.network.RandomUserApiService
import retrofit2.HttpException
import java.io.IOException

// The query parameter is passed from the Repository, if you need to fetch
// different data sets based on a query. For now, we assume a general fetch.
@OptIn(ExperimentalPagingApi::class)
class UserRemoteMediator(
    private val apiService: RandomUserApiService,
    private val appDatabase: AppDatabase,
    private val query: String // Potentially for API if it supports server-side search for remote mediator
) : RemoteMediator<Int, User>() {

    private val userDao = appDatabase.userDao()
    private val remoteKeysDao = appDatabase.remoteKeysDao()
    private val initialPage = 1

    override suspend fun load(loadType: LoadType, state: PagingState<Int, User>): MediatorResult {
        val page: Int = when (loadType) {
            LoadType.REFRESH -> {
                // Get the closest RemoteKeys to the PagingState's anchorPosition.
                // If no anchorPosition, then load the initial page.
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: initialPage
            }
            LoadType.PREPEND -> {
                // Prepending is typically not supported by "page number" based APIs like this one
                // if we can't go to page < 1. We signal success with no more data to prepend.
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            // The API uses 'page' and 'results' (for page size)
            val apiResponse = apiService.getUsers(page = page, results = state.config.pageSize)
            val networkUsers = apiResponse.results
            val endOfPaginationReached = networkUsers.isEmpty()

            appDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    // Clear old data on refresh
                    remoteKeysDao.clearRemoteKeys()
                    userDao.clearUsers()
                }

                val prevKey = if (page == initialPage) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1

                val usersToInsert = networkUsers.map { networkUser ->
                    // Map NetworkUser to User (Room entity)
                    User(
                        uuid = networkUser.login.uuid,
                        name = networkUser.name, // Assumes Name is @Embedded or a TypeConverter is set up
                        email = networkUser.email,
                        picture = networkUser.picture // Assumes Picture is @Embedded or a TypeConverter is set up
                    )
                }
                userDao.insertAll(usersToInsert)

                val keys = usersToInsert.map {
                    RemoteKeys(userId = it.uuid, prevKey = prevKey, nextKey = nextKey)
                }
                remoteKeysDao.insertAll(keys)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, User>): RemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { user -> remoteKeysDao.getRemoteKeysByUserId(user.uuid) }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, User>): RemoteKeys? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { user -> remoteKeysDao.getRemoteKeysByUserId(user.uuid) }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, User>): RemoteKeys? {
        // The anchorPosition is the most recently accessed index
        return state.anchorPosition?.let {
            state.closestItemToPosition(it)?.uuid?.let {
                userId -> remoteKeysDao.getRemoteKeysByUserId(userId)
            }
        }
    }
}
