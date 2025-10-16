package com.example.myjetpack1.di

import android.content.Context
import androidx.room.Room
import com.example.myjetpack1.db.AppDatabase
import com.example.myjetpack1.db.CountryDao
import com.example.myjetpack1.db.CurrencyDao
import com.example.myjetpack1.db.FiatDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).fallbackToDestructiveMigration()
            .build()
    }


    @Provides
    fun provideCountryDao(appDatabase: AppDatabase): CountryDao {
        return appDatabase.countryDao()
    }

    @Provides
    fun provideCurrencyDao(appDatabase: AppDatabase): CurrencyDao {
        return appDatabase.currencyDao()
    }

    @Provides
    fun provideFiatDao(appDatabase: AppDatabase): FiatDao {
        return appDatabase.fiatDao()
    }
}
