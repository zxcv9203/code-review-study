package org.example.codereviewstudy.common.exception

import org.springframework.http.HttpStatus

abstract class AuthenticationException(
    message: String,
    status: HttpStatus = HttpStatus.UNAUTHORIZED,
    private val value: Any? = null,
): BusinessException(status, message) {
    override val errorMessage: String
        get() = "$message 인증 오류: $value"
}