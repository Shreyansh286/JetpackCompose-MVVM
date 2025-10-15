package com.example.myjetpack1.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myjetpack1.model.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<User>)


    @Query("SELECT * FROM users WHERE (:query = '' OR first LIKE '%' || :query || '%' OR last LIKE '%' || :query || '%' OR email LIKE '%' || :query || '%')")
    fun getUsers(query: String): PagingSource<Int, User>

    @Query("DELETE FROM users")
    suspend fun clearUsers()
}
