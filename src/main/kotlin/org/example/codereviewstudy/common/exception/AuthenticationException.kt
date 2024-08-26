package org.example.codereviewstudy.common.exception

import org.springframework.http.HttpStatus

abstract class AuthenticationException(
    message: String = "사용자 이름 혹은 비밀번호를 다시 확인해주세요.",
    status: HttpStatus = HttpStatus.UNAUTHORIZED,
    private val value: Any? = null,
): BusinessException(status, message) {
    override val errorMessage: String
        get() = "$message 입력 값: $value"
}