package com.example.myapplication.di

import android.app.Application
import com.example.myapplication.data.database.AppDatabase
import com.example.myapplication.data.database.GroupWifiDao
import com.example.myapplication.data.database.WifiDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideWifiDao(application: Application): WifiDao =
        AppDatabase.getInstance(application).getWfiDao()

    @Provides
    @Singleton
    fun provideGroupWifiDao(application: Application): GroupWifiDao =
        AppDatabase.getInstance(application).getGroupWifiDao()
}
