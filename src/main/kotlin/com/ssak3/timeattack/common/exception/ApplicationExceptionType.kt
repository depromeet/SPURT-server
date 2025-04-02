package com.ssak3.timeattack.common.exception

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

    // ======================== [START] TASK ========================

    /**
     * - {0} : Task Type Name
     */
    TASK_TYPE_NOT_FOUND_BY_NAME(HttpStatus.BAD_REQUEST, "ERR_TASK_001", "해당 이름의 Task Type을 찾을 수 없습니다. : {0}"),

    /**
     * - {0} : Task Mode Name
     */
    TASK_MODE_NOT_FOUND_BY_NAME(HttpStatus.BAD_REQUEST, "ERR_TASK_002", "해당 이름의 Task Mode를 찾을 수 없습니다. : {0}"),

    /**
     * - {0} : Task Type Name
     * - {1} : Task Mode Name
     */
    PERSONA_NOT_FOUND_BY_TASK_KEYWORD_COMBINATION(
        HttpStatus.BAD_REQUEST,
        "ERR_TASK_003",
        "해당 Task Type({0})와 Task Mode({1})의 Persona를 찾을 수 없습니다.",
    ),

    /**
     * - {0} : Task ID
     */
    TASK_NOT_FOUND_BY_ID(
        HttpStatus.BAD_REQUEST,
        "ERR_TASK_004",
        "해당 ID로 Task를 찾을 수 없습니다. : {0}",
    ),

    /**
     * - {0} : 현재 Task Status
     * - {1} : 시도하려는 Task Status
     */
    TASK_INVALID_STATE_TRANSITION(
        HttpStatus.BAD_REQUEST,
        "ERR_TASK_005",
        "현재 상태({0})에서 요청한 상태({1})로 변경할 수 없습니다. 유효한 상태 전환만 허용됩니다.",
    ),

    /**
     * - {0} : Task ID
     * - {1} : Member ID
     */
    TASK_OWNER_MISMATCH(
        HttpStatus.BAD_REQUEST,
        "ERR_TASK_006",
        "해당 Task({0})는 회원({1})이 소유하고 있지 않습니다.",
    ),

    /**
     * - {0} : Trigger Action Alarm Time
     * - {1} : Task due datetime
     * - {2} : Estimated Time
     */
    INVALID_TRIGGER_ACTION_ALARM_TIME(
        HttpStatus.BAD_REQUEST,
        "ERR_TASK_007",
        "작은 행동 알림 시간({0})으로부터 마감({1})까지 남은 시간이 예상 소요 시간({2}분)보다 적습니다.",
    ),

    /**
     * TaskCategory는 서버측에서 관리하기 때문에 오류가 났다면 서버 로직 오류일 가능성이 높다.
     * - {0} : Task Category
     */
    TASK_CATEGORY_MISMATCH(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "ERR_TASK_008",
        "잘못된 Task Category입니다. : {0}",
    ),

    /**
     * - {0} : Reminder Alarm Time
     * - {1} : Task Due Datetime
     */
    INVALID_REMINDER_ALARM_TIME(
        HttpStatus.BAD_REQUEST,
        "ERR_TASK_009",
        "리마인드 알림 시간({0})이 마감 시간({1}) 이후입니다.",
    ),

    /**
     * - {0} : Task attribute
     * - {1} : Task Status
     */
    INVALID_TASK_STATUS_FOR_UPDATE(
        HttpStatus.BAD_REQUEST,
        "ERR_TASK_010",
        "Task의 {0} 속성은 {1} 상태에서 수정할 수 없습니다.",
    ),

    /**
     * - {0} : Due Datetime
     * - {1} : Trigger Action Alarm Time
     */
    INVALID_DUE_DATETIME(
        HttpStatus.BAD_REQUEST,
        "ERR_TASK_011",
        "마감시간({0})이 작은 행동 알림 시간({1})보다 이전일 수 없습니다.",
    ),

    // ======================== [END] TASK ========================

    // ======================== [START] SUBTASK ========================

    /**
     * - {0} : Subtask ID
     */
    SUBTASK_NOT_FOUND_BY_ID(
        HttpStatus.BAD_REQUEST,
        "ERR_SUBTASK_001",
        "해당 ID로 Subtask를 찾을 수 없습니다. : {0}",
    ),

    // ======================== [END] SUBTASK ========================

    /**
     * - {0} : BindException 에러 메시지
     */
    BIND_EXCEPTION(HttpStatus.BAD_REQUEST, "ERR_GLOBAL_001", "Request 데이터 처리 중 오류가 발생했습니다. : {0}"),

    // ======================== [START] JWT ========================

    /**
     * {0} : JWT 파싱 에러 중 서명 검증 실패 메시지
     */
    JWT_INVALID_SIGNATURE(HttpStatus.BAD_REQUEST, "ERR_JWT_001", "잘못된 서명입니다. : {0}"),

    /**
     * {0} : JWT 파싱 에러 중 토큰 만료 에러 메시지
     */
    JWT_EXPIRED(HttpStatus.UNAUTHORIZED, "ERR_JWT_002", "만료된 토큰입니다. : {0}"),

    /**
     * {0} : JWT 파싱 에러 중 잘못된 형식 에러 메시지
     */
    JWT_MALFORMED(HttpStatus.BAD_REQUEST, "ERR_JWT_003", "잘못된 형식의 토큰입니다. : {0}"),

    /**
     * {0} : JWT 파싱 에러 중 지원하지 않는 형식의 JWT 에러 메시지
     */
    JWT_UNSUPPORTED(HttpStatus.BAD_REQUEST, "ERR_JWT_004", "지원하지 않는 JWT 형식입니다. : {0}"),

    /**
     * 헤더에 Access 토큰 없는 경우
     */
    JWT_ACCESS_NOT_FOUND_IN_HEADER(HttpStatus.UNAUTHORIZED, "ERR_JWT_005", "헤더에 Access 토큰이 존재하지 않습니다."),

    /**
     * Redis에 Refresh token 없는 경우
     */
    JWT_REFRESH_NOT_FOUND_IN_REDIS(HttpStatus.UNAUTHORIZED, "ERR_JWT_007", "존재하지 않는 Refresh 토큰입니다."),

    /**
     * Redis에 저장된 Refresh token과 일치하지 않는 경우
     */
    JWT_REFRESH_INVALID(HttpStatus.UNAUTHORIZED, "ERR_JWT_008", "저장된 Refresh 토큰과 일치하지 않습니다."),

    /**
     * {0} : JWT 파싱 에러 중 발생할 수 있는 모든 에러
     */
    JWT_GENERAL_ERR(HttpStatus.BAD_REQUEST, "ERR_JWT_999", "예상하지 못한 JWT 에러입니다. : {0}"),

    // ======================== [END] JWT ========================

    // ======================== [START] AUTHENTICATION ========================
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "ERR_AUTH_001", "인증된 사용자 정보가 없습니다."),

    APPLE_REFRESH_TOKEN_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "ERR_AUTH_002", "apple refresh 토큰이 존재하지 않습니다."),

    // ======================== [END] AUTHENTICATION ========================

    // ======================== [START] OIDC ========================

    OIDC_PUBLIC_KEY_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "ERR_OIDC_001", "일치하는 공개키를 찾을 수 없습니다."),

    // ======================== [END] OIDC ========================

    // ======================== [START] RETROSPECTION ========================

    /**
     * - {0} : Task ID
     * - {1} : Task Status
     */
    CREATE_RETROSPECTION_NOT_ALLOWED(
        HttpStatus.BAD_REQUEST,
        "ERR_RETRO_001",
        "현재 task({0})의 상태가 {1} 이기에 회고 생성이 허용되지 않습니다.(COMPLETE or FOCUSED인 task에 대해서만 회고 생성 가능)",
    ),

    // ======================== [END] RETROSPECTION ========================

    // ======================== [START] PERSONA ========================

    /**
     * - {0} : Persona ID
     */
    PERSONA_NOT_FOUND_BY_ID(
        HttpStatus.BAD_REQUEST,
        "ERR_PERSONA_001",
        "해당 ID로 Persona를 찾을 수 없습니다. : {0}",
    ),

    // ======================== [END] PERSONA ========================

    // ======================== [START] GLOBAL ========================

    /**
     * - {0} : Exception Message
     */
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "ERR_GLOBAL_001", "잘못된 요청 값입니다. : {0}"),

    /**
     * - {0} : Exception Message
     */
    INVALID_UPDATE_VALUE(
        HttpStatus.BAD_REQUEST,
        "ERR_GLOBAL_002",
        "수정할 값이 유효하지 않습니다. : {0}",
    ),

    /**
     * - {0} : Exception Message
     */
    FILTER_EXCEPTION(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "ERR_GLOBAL_003",
        "Filter 에러 중 처리되지 못한 에러 발생 : {0}",
    ),

    /**
     * - {0} : Custom Exception Message
     */
    UNDEFINED_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "ERR_GLOBAL_999", "정의되지 않은 에러입니다. : {0}"),
    ;

    fun getErrorMessage(vararg args: Any): String {
        return MessageFormat.format(errorMessage, *args)
    }
}
