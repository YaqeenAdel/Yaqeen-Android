package com.cancer.yaqeen.data.di

import com.auth0.android.Auth0
import com.auth0.android.request.DefaultClient
import com.cancer.yaqeen.BuildConfig.AUTH_0_CLIENT_ID
import com.cancer.yaqeen.BuildConfig.AUTH_0_DOMAIN
import com.cancer.yaqeen.BuildConfig.BASE_URL
import com.cancer.yaqeen.data.network.AUTH
import com.cancer.yaqeen.data.network.DEFAULT
import com.cancer.yaqeen.data.network.apis.Auth0API
import com.cancer.yaqeen.data.network.apis.YaqeenAPI
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {
    @Singleton
    @Provides
    @DEFAULT
    fun provideYaqeenRetrofit(httpClient: OkHttpClient): Retrofit.Builder =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().serializeNulls().create()))

    @Singleton
    @Provides
    @AUTH
    fun provideAuthRetrofit(httpClient: OkHttpClient): Retrofit.Builder =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().serializeNulls().create()))

    @Singleton
    @Provides
    fun provideYaqeenAPIService(@DEFAULT retrofit: Retrofit.Builder): YaqeenAPI =
        retrofit.build().create(YaqeenAPI::class.java)

    @Singleton
    @Provides
    fun provideAuth0APIService(@AUTH retrofit: Retrofit.Builder): Auth0API =
        retrofit.build().create(Auth0API::class.java)

    @Singleton
    @Provides
    fun provideAuth0(): Auth0 {
        val auth0 = Auth0(
            AUTH_0_CLIENT_ID, AUTH_0_DOMAIN
        )

        // Only enable network traffic logging on production environments!
//        auth0.networkingClient = DefaultClient(enableLogging = true)

        return auth0
    }

}