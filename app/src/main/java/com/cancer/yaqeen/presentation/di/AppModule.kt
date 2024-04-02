package com.cancer.yaqeen.presentation.di

import android.content.Context
import com.cancer.yaqeen.presentation.util.NotificationUtils

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesMyNotificationManager(@ApplicationContext context: Context): NotificationUtils =
        NotificationUtils(context)


}