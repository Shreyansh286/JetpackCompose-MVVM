package com.example.myjetpack1.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myjetpack1.model.CountryDataItem
import com.example.myjetpack1.model.Data
import com.example.myjetpack1.model.User

@Database(entities = [User::class, RemoteKeys::class, CountryDataItem::class,  Data::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun remoteKeysDao(): RemoteKeysDao
    abstract fun countryDao(): CountryDao
    abstract fun currencyDao(): CurrencyDao

}
