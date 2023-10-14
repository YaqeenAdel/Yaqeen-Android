package com.cancer.yaqeen.data.features.auth

import android.content.Context
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.provider.WebAuthProvider
import com.cancer.yaqeen.BuildConfig.AUTH_0_SCHEMA
import com.cancer.yaqeen.BuildConfig.AUTH_0_URL
import com.cancer.yaqeen.data.network.base.BaseDataSource
import com.cancer.yaqeen.data.network.base.DataState
import com.cancer.yaqeen.data.features.auth.mappers.MappingLoginRemoteAsUser
import com.cancer.yaqeen.data.features.auth.models.User
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.data.network.error.ErrorHandlerImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth0: Auth0,
    errorHandler: ErrorHandlerImpl,
    private val sharedPrefEncryptionUtil: SharedPrefEncryptionUtil
): BaseDataSource(errorHandler), IAuthRepository {

    override suspend fun login(context: Context): Flow<DataState<User>> =
        withContext(Dispatchers.Main){
            try {
                val credentials = WebAuthProvider.login(auth0)
                    .withScheme(AUTH_0_SCHEMA)
                    .withScope("openid profile email read:current_user update:current_user_metadata")
                    .withAudience(AUTH_0_URL)
                    .await(context)

                sharedPrefEncryptionUtil.setToken(credentials.accessToken)
                flow {
                    emit(
                        DataState.Success(
                            MappingLoginRemoteAsUser().map(credentials.user)
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
            }
        }
}