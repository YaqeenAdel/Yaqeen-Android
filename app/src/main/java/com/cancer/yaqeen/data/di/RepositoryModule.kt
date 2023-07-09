package com.cancer.yaqeen.data.di

import com.cancer.yaqeen.data.features.auth.AuthRepositoryImpl
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.data.network.YaqeenAPI
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
        apiService: YaqeenAPI,
        errorHandlerImpl: ErrorHandlerImpl,
        sharedPrefEncryptionUtil: SharedPrefEncryptionUtil
    ) = AuthRepositoryImpl(apiService, errorHandlerImpl, sharedPrefEncryptionUtil)

}