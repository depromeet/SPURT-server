package com.ssak3.timeattack.common.validations

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [NotAllFieldNullValidator::class])
annotation class NotAllFieldNull(
    val message: String = "모든 필드를 null로 설정할 수 없습니다.",
    val groups: Array<KClass<out Any>> = [],
    val payload: Array<KClass<out Payload>> = [],
)
