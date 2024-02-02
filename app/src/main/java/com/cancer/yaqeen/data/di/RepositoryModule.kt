package com.cancer.yaqeen.data.di

import com.auth0.android.Auth0
import com.cancer.yaqeen.data.features.auth.AuthRepositoryImpl
import com.cancer.yaqeen.data.features.home.articles.HomeRepositoryImpl
import com.cancer.yaqeen.data.features.onboarding.OnboardingRepositoryImpl
import com.cancer.yaqeen.data.features.profile.ProfileRepositoryImpl
import com.cancer.yaqeen.data.features.home.schedule.ScheduleRepositoryImpl
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.data.network.apis.YaqeenAPI
import com.cancer.yaqeen.data.network.error.ErrorHandlerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideAuthRepository(
        auth0: Auth0,
        errorHandlerImpl: ErrorHandlerImpl,
        sharedPrefEncryptionUtil: SharedPrefEncryptionUtil
    ) = AuthRepositoryImpl(auth0, errorHandlerImpl, sharedPrefEncryptionUtil)
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
        errorHandlerImpl: ErrorHandlerImpl,
        sharedPrefEncryptionUtil: SharedPrefEncryptionUtil
    ) = HomeRepositoryImpl(apiService, errorHandlerImpl, sharedPrefEncryptionUtil)
    @Singleton
    @Provides
    fun provideScheduleRepository(
        apiService: YaqeenAPI,
        errorHandlerImpl: ErrorHandlerImpl,
        sharedPrefEncryptionUtil: SharedPrefEncryptionUtil
    ) = ScheduleRepositoryImpl(apiService, errorHandlerImpl, sharedPrefEncryptionUtil)

}