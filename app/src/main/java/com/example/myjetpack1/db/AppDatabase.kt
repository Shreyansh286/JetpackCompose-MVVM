package com.example.myjetpack1.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myjetpack1.model.CountryDataItem
import com.example.myjetpack1.model.Data

@Database(entities = [CountryDataItem::class,  Data::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun countryDao(): CountryDao
    abstract fun currencyDao(): CurrencyDao
    abstract fun fiatDao(): FiatDao
}
