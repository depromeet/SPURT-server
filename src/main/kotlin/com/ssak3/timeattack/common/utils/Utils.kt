package com.ssak3.timeattack.common.utils

/**
 * value가 null이 아닌지 체크합니다.
 * null 이라면 "${valueName} must not be null" 이라는 예외 메시지를 가진 IllegalArgumentException을 발생시킵니다.
 * @param value 체크할 값
 * @param valueName 예외 메시지에 사용할 속성 이름
 */
fun <T : Any> checkNotNull(
    value: T?,
    valueName: String?,
): T {
    val message = valueName?.let { "$it must not be null" } ?: "Required value was null."
    return checkNotNull(value) { message }
}
