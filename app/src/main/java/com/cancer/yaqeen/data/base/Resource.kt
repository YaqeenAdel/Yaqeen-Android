package com.cancer.yaqeen.data.base

import com.cancer.yaqeen.data.network.error.ErrorEntity

enum class Status { LOADING, SUCCESS, ERROR, FAILED, EMPTY }

sealed class Resource<T>(
    val data: T? = null,
    val status: Status,
    val errorEntity: ErrorEntity? = null
) {

    class Success<T>(data: T? = null) :
        Resource<T>(data = data, status = Status.SUCCESS)

    class Loading<T> : Resource<T>(status = Status.LOADING)

    class Error<T>(error: ErrorEntity?, data: T? = null) :
        Resource<T>(status = Status.ERROR, errorEntity = error, data = data)

    class Empty<T> : Resource<T>(status = Status.EMPTY)

    class Failed<T>(data: T? = null) :
        Resource<T>(status = Status.FAILED, data = data)

}