package com.cancer.yaqeen.data.di

import android.content.Context
import com.auth0.android.Auth0
import com.cancer.yaqeen.data.features.auth.AuthRepositoryImpl
import com.cancer.yaqeen.data.features.home.articles.HomeRepositoryImpl
import com.cancer.yaqeen.data.features.onboarding.OnboardingRepositoryImpl
import com.cancer.yaqeen.data.features.profile.ProfileRepositoryImpl
import com.cancer.yaqeen.data.features.home.schedule.ScheduleRepositoryImpl
import com.cancer.yaqeen.data.features.home.schedule.data_sources.ScheduleLocalDataSourceImpl
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.data.network.apis.Auth0API
import com.cancer.yaqeen.data.network.apis.YaqeenAPI
import com.cancer.yaqeen.data.network.error.ErrorHandlerImpl
import com.cancer.yaqeen.data.room.YaqeenDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideAuthRepository(
        auth0: Auth0,
        authAPI: Auth0API,
        errorHandlerImpl: ErrorHandlerImpl,
        sharedPrefEncryptionUtil: SharedPrefEncryptionUtil
    ) = AuthRepositoryImpl(auth0, authAPI, errorHandlerImpl, sharedPrefEncryptionUtil)
    @Singleton
    @Provides
    fun provideOnboardingRepository(
        apiService: YaqeenAPI,
        errorHandlerImpl: ErrorHandlerImpl,
        sharedPrefEncryptionUtil: SharedPrefEncryptionUtil
    ) = OnboardingRepositoryImpl(apiService, errorHandlerImpl, sharedPrefEncryptionUtil)
    @Singleton
    @Provides
    fun provideProfileRepository(
        apiService: YaqeenAPI,
        errorHandlerImpl: ErrorHandlerImpl,
        sharedPrefEncryptionUtil: SharedPrefEncryptionUtil
    ) = ProfileRepositoryImpl( errorHandlerImpl, sharedPrefEncryptionUtil)
    @Singleton
    @Provides
    fun provideHomeRepository(
        apiService: YaqeenAPI,
        yaqeenDao: YaqeenDao,
        errorHandlerImpl: ErrorHandlerImpl,
        sharedPrefEncryptionUtil: SharedPrefEncryptionUtil
    ) = HomeRepositoryImpl(apiService, yaqeenDao, errorHandlerImpl, sharedPrefEncryptionUtil)
    @Singleton
    @Provides
    fun provideScheduleRepository(
        @ApplicationContext context: Context,
        apiService: YaqeenAPI,
        scheduleLocalDataSourceImpl: ScheduleLocalDataSourceImpl,
        errorHandlerImpl: ErrorHandlerImpl,
        sharedPrefEncryptionUtil: SharedPrefEncryptionUtil
    ) = ScheduleRepositoryImpl(context, apiService, scheduleLocalDataSourceImpl, errorHandlerImpl, sharedPrefEncryptionUtil)

}