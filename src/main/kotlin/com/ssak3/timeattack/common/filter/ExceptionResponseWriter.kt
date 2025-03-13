package com.ssak3.timeattack.common.filter

import com.fasterxml.jackson.databind.ObjectMapper
import com.ssak3.timeattack.common.exception.ApplicationException
import com.ssak3.timeattack.common.exception.ApplicationExceptionType
import com.ssak3.timeattack.common.response.ExceptionResponse
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import java.io.IOException
import java.nio.charset.StandardCharsets

class ExceptionResponseWriter {
    companion object {
        @Throws(IOException::class)
        fun writeException(
            response: HttpServletResponse,
            exceptionType: ApplicationExceptionType,
            vararg args: Any?,
        ) {
            setResponseInfo(response, exceptionType.httpStatus)
            val writer = response.writer
            val mapper = ObjectMapper()
            writer.write(mapper.writeValueAsString(ExceptionResponse.from(exceptionType, args)))
            writer.flush()
        }

        @Throws(IOException::class)
        fun writeException(
            response: HttpServletResponse,
            exception: ApplicationException,
        ) {
            setResponseInfo(response, exception.exceptionType.httpStatus)
            val writer = response.writer
            val mapper = ObjectMapper()
            writer.write(mapper.writeValueAsString(ExceptionResponse.from(exception)))
            writer.flush()
        }

        private fun setResponseInfo(
            response: HttpServletResponse,
            httpStatus: HttpStatus,
        ) {
            response.status = httpStatus.value()
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.characterEncoding = StandardCharsets.UTF_8.name()
        }
    }
}
