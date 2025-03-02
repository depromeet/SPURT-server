package com.ssak3.timeattack.common.config

import com.ssak3.timeattack.common.utils.Logger
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import java.lang.reflect.Method

@Configuration
@EnableAsync
class AsyncConfig : AsyncConfigurer {
    override fun getAsyncUncaughtExceptionHandler(): AsyncUncaughtExceptionHandler {
        return AsyncExceptionHandler()
    }
}

class AsyncExceptionHandler : AsyncUncaughtExceptionHandler, Logger {
    override fun handleUncaughtException(
        exception: Throwable,
        method: Method,
        vararg params: Any,
    ) {
        logger.error("비동기 이벤트 처리 중 예외 발생: ${exception.message}", exception)
    }
}
