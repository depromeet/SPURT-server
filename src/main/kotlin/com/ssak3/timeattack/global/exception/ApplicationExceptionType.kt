package com.ssak3.timeattack.global.exception

import org.springframework.http.HttpStatus
import java.text.MessageFormat

enum class ApplicationExceptionType(
    val httpStatus: HttpStatus,
    val exceptionCode: String,
    private val errorMessage: String,
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
     * {0} : JWT 파싱 에러 중 서명 검증 실패 메시지
     */
    JWT_INVALID_SIGNATURE(HttpStatus.BAD_REQUEST, "ERR_JWT_001", "잘못된 서명입니다. : {0}"),

    /**
     * {0} : JWT 파싱 에러 중 토큰 만료 에러 메시지
     */
    JWT_EXPIRED(HttpStatus.BAD_REQUEST, "ERR_JWT_002", "만료된 토큰입니다. : {0}"),

    /**
     * {0} : JWT 파싱 에러 중 잘못된 형식 에러 메시지
     */
    JWT_MALFORMED(HttpStatus.BAD_REQUEST, "ERR_JWT_003", "잘못된 형식의 토큰입니다. : {0}"),

    /**
     * {0} : JWT 파싱 에러 중 지원하지 않는 형식의 JWT 에러 메시지
     */
    JWT_UNSUPPORTED(HttpStatus.BAD_REQUEST, "ERR_JWT_004", "지원하지 않는 JWT 형식입니다. : {0}"),

    /**
     * Cookie에 JWT 토큰 없는 경우
     */
    JWT_NOT_FOUND(HttpStatus.UNAUTHORIZED, "ERR_JWT_005", "쿠키에 JWT 토큰이 존재하지 않습니다."),

    /**
     * {0} : JWT 파싱 에러 중 발생할 수 있는 모든 에러
     */
    JWT_GENERAL_ERR(HttpStatus.BAD_REQUEST, "ERR_JWT_999", "예상하지 못한 JWT 에러입니다. : {0}"),

    OIDC_PUBLIC_KEY_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "ERR_OIDC_001", "일치하는 공개키를 찾을 수 없습니다."),

    /**
     * - {0} : Custom Exception Message
     */
    UNDEFINED_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "ERR_GLOBAL_999", "정의되지 않은 에러입니다. : {0}"),
    ;

    fun getErrorMessage(vararg args: Any): String {
        return MessageFormat.format(errorMessage, *args)
    }
}
