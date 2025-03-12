package com.ssak3.timeattack.task.controller.dto.validations

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class AllowedIntValuesValidator : ConstraintValidator<AllowedIntValues, Int> {
    private lateinit var allowedIntValues: Set<Int>

    override fun initialize(constraintAnnotation: AllowedIntValues) {
        allowedIntValues = constraintAnnotation.values.toSet()
    }

    override fun isValid(
        value: Int?,
        context: ConstraintValidatorContext,
    ): Boolean {
        return value != null && value in allowedIntValues
    }
}
