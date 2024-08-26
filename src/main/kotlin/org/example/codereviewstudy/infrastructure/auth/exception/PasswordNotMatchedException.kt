package org.example.codereviewstudy.infrastructure.auth.exception

import org.example.codereviewstudy.common.exception.AuthenticationException
import org.springframework.http.HttpStatus

class PasswordNotMatchedException(
    private val encryptPassword: String,
    val password: String,
) : AuthenticationException(
    status = HttpStatus.BAD_REQUEST,
    value = password,
) {
    override val errorMessage: String
        get() = "비밀번호가 일치하지 않습니다. 입력된 비밀번호: $password $encryptPassword"
}