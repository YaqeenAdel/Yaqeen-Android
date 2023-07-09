package com.codesroots.imakeapp.core.network.di

import android.content.Context
import android.content.SharedPreferences
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.data.utils.Constants.APP_PREFERENCES

import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalStorageModule {

    @Provides
    @Singleton
    fun providesSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences(
            APP_PREFERENCES,
            Context.MODE_PRIVATE
        )

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideSharedPrefEncryptionUtil(
        sharedPreferences: SharedPreferences,
        gson: Gson,
        @ApplicationContext context: Context
    ) =
        SharedPrefEncryptionUtil(sharedPreferences = sharedPreferences, gson = gson)
}