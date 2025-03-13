package com.ssak3.timeattack.common.filter

import com.ssak3.timeattack.common.exception.ApplicationException
import com.ssak3.timeattack.common.exception.ApplicationExceptionType
import com.ssak3.timeattack.common.utils.Logger
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter

class ExceptionHandlerFilter : OncePerRequestFilter(), Logger {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {
            filterChain.doFilter(request, response)
        } catch (exception: ApplicationException) {
            logger.error("Filter에서 ApplicationException 발생", exception)
            ExceptionResponseWriter.writeException(response, exception)
        } catch (exception: Exception) {
            logger.error("Filter에서 Exception 발생", exception)
            ExceptionResponseWriter.writeException(
                response,
                ApplicationExceptionType.FILTER_EXCEPTION,
                exception.localizedMessage,
            )
        }
    }
}
