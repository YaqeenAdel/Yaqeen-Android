package com.cancer.yaqeen.data.di

import com.auth0.android.Auth0
import com.cancer.yaqeen.BuildConfig
import com.cancer.yaqeen.data.network.AUTH
import com.cancer.yaqeen.data.network.DEFAULT
import com.cancer.yaqeen.data.network.apis.Auth0API
import com.cancer.yaqeen.data.network.apis.YaqeenAPI
import com.google.gson.FieldNamingPolicy
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
    fun provideGsonBuilder(): GsonBuilder = GsonBuilder()
        .setLenient()
        .serializeNulls()
//        .setPrettyPrinting()
//        .disableHtmlEscaping()
//        .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)

    @Singleton
    @Provides
    @DEFAULT
    fun provideYaqeenRetrofit(httpClient: OkHttpClient, gsonBuilder: GsonBuilder): Retrofit.Builder =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(httpClient)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))

    @Singleton
    @Provides
    @AUTH
    fun provideAuthRetrofit(httpClient: OkHttpClient, gsonBuilder: GsonBuilder): Retrofit.Builder =
        Retrofit.Builder()
            .baseUrl("https://${BuildConfig.AUTH_0_DOMAIN}/")
            .client(httpClient)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))

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
            BuildConfig.AUTH_0_CLIENT_ID, BuildConfig.AUTH_0_DOMAIN
        )

        // Only enable network traffic logging on production environments!
//        auth0.networkingClient = DefaultClient(enableLogging = true)

        return auth0
    }

}