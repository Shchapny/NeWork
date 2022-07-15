package ru.netology.diplom.error

import java.io.IOException
import java.net.SocketTimeoutException
import java.sql.SQLException

sealed class AppError(val code: Int, info: String) : RuntimeException(info) {
    companion object {
        fun from(e: Throwable): AppError = when (e) {
            is AppError -> e
            is IOException -> NetworkError
            is SocketTimeoutException -> ServerError
            is SQLException -> DbError
            else -> UnknownError
        }
    }
}

class ApiError(code: Int, infoWarning: String) : AppError(code, infoWarning)
object NetworkError : AppError(-1,"Network error")
object ServerError : AppError(-1,"Server error")
object DbError : AppError(-1,"Database error")
object UnknownError : AppError(-1,"Unknown error")
