package com.ssak3.timeattack.task.controller.dto.validations

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Constraint(validatedBy = [AllowedIntValuesValidator::class])
annotation class AllowedIntValues(
    val values: IntArray,
    val message: String = "Invalid value. Allowed values are: {values}",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)
