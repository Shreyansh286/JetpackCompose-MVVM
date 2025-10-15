package com.example.myjetpack1.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.myjetpack1.model.User
import com.example.myjetpack1.network.RandomUserApiService
import retrofit2.HttpException
import java.io.IOException

class UserPagingSource(
    private val apiService: RandomUserApiService,
    private val query: String // For search functionality later
) : PagingSource<Int, User>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        val page = params.key ?: 1 // API pages are 1-indexed
        return try {
            val response = apiService.getUsers(page = page, results = params.loadSize)
            
            // Map NetworkUser to User (Room entity)
            val mappedUsers = response.results.map { networkUser ->
                User(
                    uuid = networkUser.login.uuid,
                    name = networkUser.name,       // Name class is directly usable
                    email = networkUser.email,
                    picture = networkUser.picture  // Picture class is directly usable
                )
            }

            val users = mappedUsers.filter { user ->
                // Basic filtering for name or email on the mapped User objects
                if (query.isBlank()) {
                    true
                } else {
                    // Ensure User entity has name.first, name.last, and email fields
                    val fullName = "${user.name.first} ${user.name.last}".lowercase()
                    val email = user.email.lowercase()
                    fullName.contains(query.lowercase()) || email.contains(query.lowercase())
                }
            }

            LoadResult.Page(
                data = users, // This is now List<User>
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (users.isEmpty() && query.isBlank()) null else page + 1 
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, User>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
