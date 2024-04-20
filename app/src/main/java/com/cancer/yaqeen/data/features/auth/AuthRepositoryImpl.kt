package com.cancer.yaqeen.data.features.auth

import android.content.Context
import android.util.Log
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.authentication.storage.CredentialsManager
import com.auth0.android.authentication.storage.CredentialsManagerException
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import com.auth0.android.callback.BaseCallback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.cancer.yaqeen.BuildConfig.AUTH_0_CLIENT_ID
import com.cancer.yaqeen.BuildConfig.AUTH_0_SCHEMA
import com.cancer.yaqeen.BuildConfig.AUTH_0_URL
import com.cancer.yaqeen.data.features.auth.mappers.MappingLoginRemoteAsUser
import com.cancer.yaqeen.data.features.auth.models.User
import com.cancer.yaqeen.data.features.auth.requests.RefreshTokenRequestBody
import com.cancer.yaqeen.data.features.auth.responses.RefreshTokenResponse
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil.Companion.PREF_USER
import com.cancer.yaqeen.data.network.apis.Auth0API
import com.cancer.yaqeen.data.network.base.BaseDataSource
import com.cancer.yaqeen.data.network.base.DataState
import com.cancer.yaqeen.data.network.base.flowStatus
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.data.network.error.ErrorHandlerImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth0: Auth0,
    private val authAPI: Auth0API,
    errorHandler: ErrorHandlerImpl,
    private val sharedPrefEncryptionUtil: SharedPrefEncryptionUtil
): BaseDataSource(errorHandler), IAuthRepository {

    override suspend fun login(context: Context): Flow<DataState<User>> =
        withContext(Dispatchers.IO){
            try {
                val apiClient = AuthenticationAPIClient(auth0)
                val manager = CredentialsManager(apiClient, SharedPreferencesStorage(context))

                val credentials = WebAuthProvider.login(auth0)
                    .withScheme(AUTH_0_SCHEMA)
                    .withParameters(mapOf("prompt" to "select_account"))
                    .withScope("openid profile email offline_access read:current_user update:current_user_metadata")
                    .withAudience(AUTH_0_URL)
                    .await(context)

                flow {
                    val user = MappingLoginRemoteAsUser().map(credentials.user)
                    sharedPrefEncryptionUtil.setToken(credentials.accessToken)
                    sharedPrefEncryptionUtil.setRefreshToken(credentials.refreshToken)
                    sharedPrefEncryptionUtil.setTokenExpiredDate(credentials.expiresAt.time)
                    sharedPrefEncryptionUtil.setTokenType(credentials.type)
                    sharedPrefEncryptionUtil.setModelData(user, PREF_USER)
                    manager.saveCredentials(credentials)

                    emit(
                        DataState.Success(
                            user
                        )
                    )
                }

            }catch (e: AuthenticationException){
                flow {
                    emit(
                        DataState.Error(
                            ErrorEntity.ApiError.Network
                        )
                    )
                }
            }catch (e: Exception){
                flow {
                    emit(
                        DataState.Error(
                            ErrorEntity.ApiError.Network
                        )
                    )
                }
            }
        }

    override suspend fun logout(context: Context): Flow<DataState<Boolean>> =
        withContext(Dispatchers.IO){
            sharedPrefEncryptionUtil.clearUserPreferenceStorage()
            try {
                val apiClient = AuthenticationAPIClient(auth0)
                val manager = CredentialsManager(apiClient, SharedPreferencesStorage(context))
                manager.clearCredentials()
                WebAuthProvider.logout(auth0)
                    .withScheme(AUTH_0_SCHEMA)
                    .await(context)


                flow {
                    emit(
                        DataState.Success(true)
                    )
                }
            }catch (e: AuthenticationException){
                flow {
                    emit(
                        DataState.Error(
                            ErrorEntity.ApiError.Network
                        )
                    )
                }
            }
        }

    override suspend fun refreshToken(context: Context){
        val authenticationClient = AuthenticationAPIClient(auth0)
        val credentialsManager = CredentialsManager(
            authenticationClient,
            SharedPreferencesStorage(context)
        )
        val refreshToken = sharedPrefEncryptionUtil.getRefreshToken()
        val timeInMillis = sharedPrefEncryptionUtil.getTokenExpiredDate()
        val currentTimestamp = System.currentTimeMillis()


        if (currentTimestamp >= timeInMillis)
            authenticationClient
                .renewAuth(refreshToken)
                .start(object : BaseCallback<Credentials, AuthenticationException> {
                    override fun onSuccess(payload: Credentials) {
                        // Save the new credentials
                        credentialsManager.saveCredentials(payload)

                        payload.run {
                            sharedPrefEncryptionUtil.setToken(accessToken)
                            sharedPrefEncryptionUtil.setRefreshToken(refreshToken)
                            sharedPrefEncryptionUtil.setTokenExpiredDate(expiresAt.time)
                            sharedPrefEncryptionUtil.setTokenType(type)
                        }
                        // Handle success - you have a new access token
                    }

                    override fun onFailure(error: AuthenticationException) {
                        // Handle failure
                    }
                })
    }

}