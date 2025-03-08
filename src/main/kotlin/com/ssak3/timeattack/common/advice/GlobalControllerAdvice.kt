package com.ssak3.timeattack.common.advice

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.ssak3.timeattack.common.utils.Logger
import com.ssak3.timeattack.common.exception.ApplicationException
import com.ssak3.timeattack.common.exception.ApplicationExceptionType
import com.ssak3.timeattack.common.response.ExceptionResponse
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.HandlerMethodValidationException

@RestControllerAdvice
class GlobalControllerAdvice : Logger {
    @ExceptionHandler(ApplicationException::class)
    fun handleApplicationException(exception: ApplicationException): ResponseEntity<ExceptionResponse> {
        logger.error("ApplicationException occurred", exception)
        return ResponseEntity.status(exception.exceptionType.httpStatus).body(ExceptionResponse.from(exception))
    }

    @ExceptionHandler(HandlerMethodValidationException::class)
    fun handleValidationException(exception: HandlerMethodValidationException): ResponseEntity<ExceptionResponse> {
        val sb = StringBuilder()
        exception.parameterValidationResults.forEachIndexed { index, it ->
            sb.append("Validation Error${index + 1}) ")
            it.resolvableErrors.forEach { error ->
                sb.append(error.defaultMessage).append(", ")
            }
            sb.append("\n")
        }
        val errorMessage = sb.toString()
        logger.error("ValidationException occurred : $errorMessage", exception)
        val exceptionType = ApplicationExceptionType.INVALID_REQUEST
        return ResponseEntity.status(
            exceptionType.httpStatus,
        ).body(ExceptionResponse.from(exceptionType, errorMessage))
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleMismatchedInputException(exception: MismatchedInputException): ResponseEntity<ExceptionResponse> {
        logger.error("HttpMessageNotReadableException occurred", exception)
        val exceptionType = ApplicationExceptionType.INVALID_REQUEST
        return ResponseEntity.status(
            exceptionType.httpStatus,
        ).body(ExceptionResponse.from(exceptionType, exception.message ?: "json format error"))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(exception: MethodArgumentNotValidException): ResponseEntity<ExceptionResponse> {
        logger.error("MethodArgumentNotValidException occurred", exception)
        val exceptionType = ApplicationExceptionType.INVALID_REQUEST
        return ResponseEntity.status(
            exceptionType.httpStatus,
        ).body(ExceptionResponse.from(exceptionType, exception.message))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception): ResponseEntity<ExceptionResponse> {
        logger.error("Exception occurred", exception)
        val exceptionType = ApplicationExceptionType.UNDEFINED_EXCEPTION
        return ResponseEntity.status(
            exceptionType.httpStatus,
        ).body(ExceptionResponse.from(exceptionType, exception.localizedMessage))
    }
}
