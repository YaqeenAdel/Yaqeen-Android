package com.cancer.yaqeen.data.network.error


sealed class ErrorEntity(val errorMessage: String = "", val errorResponse: ErrorResponse? = null) {

    sealed class ApiError(private val error: String): ErrorEntity(error) {
        object InternetConnection : ErrorEntity()
        object Network : ErrorEntity()
        object TimeOutNetwork : ErrorEntity()
        object BadResponseNetwork : ErrorEntity()
        object BadRequestNetwork : ErrorEntity()

        data class NotFound(val error: String) : ErrorEntity(error)

        object AccessDenied : ErrorEntity()

        object ServiceUnavailable : ErrorEntity()

        data class Unknown(val error: String) : ErrorEntity(error)

        data class ServerErrorResponse(val error: ErrorResponse) : ErrorEntity(errorResponse = error)

    }

    sealed class GraphQlError(private val error: String): ErrorEntity(error) {
        object ApolloNetworkException : ErrorEntity()
        data class ApolloErrorResponse(val error: String) : ErrorEntity()
    }

    sealed class FileError(val error: String): ErrorEntity(error) {

        data class NotFound(val message: String): FileError(message)

        data class ReadError(val message: String):  FileError(message)
    }

    sealed class DatabaseError(val error: String = ""): ErrorEntity(error) {
        object Unknown: ErrorEntity()
    }

}
sealed class ErrorPaging(val error: String? = null): Throwable() {
    data class NoDataAvailable(val eMessage: String? = null): ErrorPaging(eMessage)
    data class NoInternetConnection(val eMessage: String? = null): ErrorPaging(eMessage)
}