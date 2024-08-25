package org.example.codereviewstudy.infrastructure.auth.exception

import org.example.codereviewstudy.common.exception.AuthenticationException

class InvalidTokenException(
    val token: String?,
    override val message: String = "유효하지 않은 토큰입니다.",
): AuthenticationException(
    message = message,
    value = token,
)