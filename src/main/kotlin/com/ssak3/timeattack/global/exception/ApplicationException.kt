package com.ssak3.timeattack.global.exception

class ApplicationException (
    val exceptionType: ApplicationExceptionType,
    vararg args: Any
) : RuntimeException(exceptionType.getErrorMessage(*args)) {

    private val messageArguments: Array<out Any> = args

    override val message: String
        get() = exceptionType.getErrorMessage(*messageArguments)
}
