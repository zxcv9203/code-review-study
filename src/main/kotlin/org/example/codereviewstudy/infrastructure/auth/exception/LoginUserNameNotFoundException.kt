package org.example.codereviewstudy.infrastructure.auth.exception

import org.example.codereviewstudy.common.exception.AuthenticationException
import org.springframework.http.HttpStatus

class LoginUserNameNotFoundException(
    val username: String,
) : AuthenticationException(
    status = HttpStatus.BAD_REQUEST,
    value = username,
) {
    override val errorMessage: String
        get() = "사용자 이름을 찾을 수 없습니다. 입력받은 사용자 이름: $username"
}