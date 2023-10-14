package com.cancer.yaqeen.data.di

import android.content.Context
import com.auth0.android.Auth0
import com.cancer.yaqeen.data.features.auth.AuthRepositoryImpl
import com.cancer.yaqeen.data.features.onboarding.OnboardingRepositoryImpl
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.data.network.apis.YaqeenAPI
import com.cancer.yaqeen.data.network.error.ErrorHandlerImpl
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
        errorHandlerImpl: ErrorHandlerImpl,
        sharedPrefEncryptionUtil: SharedPrefEncryptionUtil
    ) = AuthRepositoryImpl(auth0, errorHandlerImpl, sharedPrefEncryptionUtil)
    @Singleton
    @Provides
    fun provideOnboardingRepository(
        apiService: YaqeenAPI,
        errorHandlerImpl: ErrorHandlerImpl
    ) = OnboardingRepositoryImpl(apiService, errorHandlerImpl)

}