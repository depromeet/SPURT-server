package com.ssak3.timeattack.global.response

import com.ssak3.timeattack.global.exception.ApplicationException
import com.ssak3.timeattack.global.exception.ApplicationExceptionType

data class ExceptionResponse (
    val httpStatus: String,
    val exceptionCode: String,
    val message: String,
) {
    companion object {
        fun from(exception: ApplicationException): ExceptionResponse {
            val exceptionType = exception.exceptionType
            return ExceptionResponse(
                exceptionType.httpStatus.toString(),
                exceptionType.exceptionCode,
                exception.message
            )
        }

        fun from(exceptionType: ApplicationExceptionType, vararg args: Any): ExceptionResponse {
            return ExceptionResponse(
                exceptionType.httpStatus.toString(),
                exceptionType.exceptionCode,
                exceptionType.getErrorMessage(*args)
            )
        }
    }
}
