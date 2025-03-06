package com.ssak3.timeattack.common.validations

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import kotlin.reflect.full.memberProperties

class NotAllFieldNullValidator : ConstraintValidator<NotAllFieldNull, Any> {
    override fun isValid(
        value: Any?,
        context: ConstraintValidatorContext,
    ): Boolean {
        // 객체 자체가 null이면 검증 실패
        if (value == null) return false

        // 클래스의 모든 속성 가져와서 검사
        val properties = value::class.memberProperties
        val hasNonNullField = properties.any { it.getter.call(value) != null }

        // 하나라도 null이 아니면 true 반환 (유효)
        return hasNonNullField
    }
}
