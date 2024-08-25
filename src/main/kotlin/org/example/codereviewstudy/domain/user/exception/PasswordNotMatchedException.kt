package org.example.codereviewstudy.domain.user.exception

import org.example.codereviewstudy.common.exception.NotMatchedException

class PasswordNotMatchedException(
    encryptPassword: String,
    val password: String,
    override val message: String = "비밀번호가 일치하지 않습니다.",
): NotMatchedException(
    message = message,
    expectedValue = password,
    actualValue = encryptPassword,
) {
    override val errorMessage: String
        get() = "$message 입력된 비밀번호: $password"
}