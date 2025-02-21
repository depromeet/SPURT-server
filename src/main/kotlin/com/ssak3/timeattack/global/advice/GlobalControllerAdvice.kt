package com.ssak3.timeattack.global.advice

import com.ssak3.timeattack.global.exception.ApplicationException
import com.ssak3.timeattack.global.exception.ApplicationExceptionType
import com.ssak3.timeattack.global.response.ExceptionResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalControllerAdvice {
    @ExceptionHandler(ApplicationException::class)
    fun handleApplicationException(exception: ApplicationException): ResponseEntity<ExceptionResponse> {
        return ResponseEntity.status(exception.exceptionType.httpStatus).body(ExceptionResponse.from(exception))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception): ResponseEntity<ExceptionResponse> {
        val exceptionType = ApplicationExceptionType.UNDEFINED_EXCEPTION
        return ResponseEntity.status(
            exceptionType.httpStatus,
        ).body(ExceptionResponse.from(exceptionType, exception.localizedMessage))
    }
}
