package com.cancer.yaqeen.data.network.error


sealed class ErrorEntity(val errorMessage: String? = "") {

    sealed class ApiError(private val error: String): ErrorEntity(error) {
        data class InternetConnection(val error: String) : ErrorEntity(error)

        data class Network(val error: String) : ErrorEntity(error)

        data class TimeOutNetwork(val error: String) : ErrorEntity(error)

        data class BadHttpResponseNetwork(val error: String) : ErrorEntity(error)

        data class BadResponseNetwork(val error: String) : ErrorEntity(error)

        data class BadRequestNetwork(val error: String) : ErrorEntity(error)

        data class NotFound(val error: String) : ErrorEntity(error)

        data class AccessDenied(val error: String) : ErrorEntity(error)

        data class ServiceUnavailable(val error: String) : ErrorEntity(error)

        data class Unknown(val error: String) : ErrorEntity(error)

        data class ServerErrorResponse(val error: ErrorResponse) : ErrorEntity(error.errorMessage)
    }

    sealed class FileError(val error: String): ErrorEntity(error) {

        data class NotFound(val message: String): FileError(message)

        data class ReadError(val message: String):  FileError(message)
    }

    sealed class DatabaseError(val error: String): ErrorEntity(error) {
        data class Unknown(val message: String): ErrorEntity(message)
    }

}

sealed class ErrorPaging(val error: String? = null): Throwable() {
    data class NoDataAvailable(val eMessage: String? = null): ErrorPaging(eMessage)
    data class NoInternetConnection(val eMessage: String? = null): ErrorPaging(eMessage)
}