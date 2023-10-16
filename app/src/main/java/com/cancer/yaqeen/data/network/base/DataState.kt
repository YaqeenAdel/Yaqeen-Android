package com.cancer.yaqeen.data.network.base

import com.cancer.yaqeen.data.network.error.ErrorEntity

sealed class DataState<T>(
    val status: Status = Status.INACTIVE,
    val loading: Boolean = false,
    val data: T? = null,
    val errorEntity: ErrorEntity? = null
) {

    class Success<T>(data: T? = null) : DataState<T>(data = data, status = Status.SUCCESS)

    class Loading<T> : DataState<T>(status = Status.LOADING, loading = true)

    class Error<T>(error: ErrorEntity?, data: T? = null) : DataState<T>(status = Status.ERROR, errorEntity = error)

    class Failed<T>() : DataState<T>(status = Status.FAILED)

}