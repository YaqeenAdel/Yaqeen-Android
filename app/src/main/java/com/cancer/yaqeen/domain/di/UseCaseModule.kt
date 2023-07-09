package com.cancer.yaqeen.domain.di

import com.cancer.yaqeen.data.features.auth.AuthRepositoryImpl
import com.cancer.yaqeen.domain.features.auth.login.usecases.LoginUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Singleton
    @Provides
    fun provideLoginUseCase(repository: AuthRepositoryImpl) =
        LoginUseCase(repository)

}