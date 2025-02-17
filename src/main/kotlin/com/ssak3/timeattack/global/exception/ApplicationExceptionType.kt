package com.ssak3.timeattack.global.exception

import org.springframework.http.HttpStatus
import java.text.MessageFormat

enum class ApplicationExceptionType (
    val httpStatus: HttpStatus,
    val exceptionCode: String,
    private val errorMessage: String
) {
    /**
     * - {0} : Member ID
     */
    MEMBER_NOT_FOUND_BY_ID(HttpStatus.BAD_REQUEST, "ERR_MEMBER_001", "해당 ID의 회원을 찾을 수 없습니다. : {0}"),

    /**
     * - {0} : BindException 에러 메시지
     */
    BIND_EXCEPTION(HttpStatus.BAD_REQUEST, "ERR_GLOBAL_001", "Request 데이터 처리 중 오류가 발생했습니다. : {0}"),

    /**
     * - {0} : Custom Exception Message
     */
    UNDEFINED_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "ERR_GLOBAL_999", "정의되지 않은 에러입니다. : {0}");

    fun getErrorMessage(vararg args: Any): String {
        return MessageFormat.format(errorMessage, *args)
    }

}
