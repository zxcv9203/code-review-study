package org.example.codereviewstudy.common.exception

import org.example.codereviewstudy.common.exception.message.ErrorMessage
import org.springframework.http.HttpStatus

abstract class AuthenticationException(
    message: String = ErrorMessage.AUTHENTICATION_FAILED.message,
    status: HttpStatus = HttpStatus.UNAUTHORIZED,
    private val value: Any? = null,
) : BusinessException(status, message) {
    override val errorMessage: String
        get() = "$message 입력 값: $value"
}