package com.cancer.yaqeen.data.features.auth

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.cancer.yaqeen.BuildConfig.AUTH_0_URL
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.base.BaseDataSource
import com.cancer.yaqeen.data.base.Resource
import com.cancer.yaqeen.data.base.flowResponseAPI
import com.cancer.yaqeen.data.features.auth.mappers.MappingLoginRemoteAsUser
import com.cancer.yaqeen.data.features.auth.models.User
import com.cancer.yaqeen.data.features.auth.requests.LoginRequestBody
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil.Companion.PREF_USER
import com.cancer.yaqeen.data.network.apis.YaqeenAPI
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.data.network.error.ErrorHandlerImpl
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val context: Context,
    private val auth0: Auth0,
    errorHandler: ErrorHandlerImpl,
    private val sharedPrefEncryptionUtil: SharedPrefEncryptionUtil
): BaseDataSource(errorHandler), IAuthRepository {

    override suspend fun login(): Flow<Resource<User>> =
        flow {
            withContext(Dispatchers.IO){
                try {
                    val credentials = WebAuthProvider.login(auth0)
                        .withScheme("demo")
                        .withScope("openid profile email read:current_user update:current_user_metadata")
                        .withAudience(AUTH_0_URL)
                        .await(context)
                    emit(
                        Resource.Success(
                            MappingLoginRemoteAsUser().map(credentials.user)
                        )
                    )
                }catch (e: AuthenticationException){
                    emit(
                        Resource.Error(
                            ErrorEntity.ApiError.Network("")
                        )
                    )
                }
            }
        }
}