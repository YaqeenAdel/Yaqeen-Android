package com.cancer.yaqeen.domain.di

import com.cancer.yaqeen.data.features.auth.AuthRepositoryImpl
import com.cancer.yaqeen.data.features.onboarding.OnboardingRepositoryImpl
import com.cancer.yaqeen.domain.features.auth.login.usecases.LoginUseCase
import com.cancer.yaqeen.domain.features.onboarding.usecases.GetResourcesUseCase
import com.cancer.yaqeen.domain.features.onboarding.usecases.UpdateUserProfileUseCase
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

    @Singleton
    @Provides
    fun provideGetResourcesUseCase(repository: OnboardingRepositoryImpl) =
        GetResourcesUseCase(repository)

    @Singleton
    @Provides
    fun provideUpdateUserProfileUseCase(repository: OnboardingRepositoryImpl) =
        UpdateUserProfileUseCase(repository)

}