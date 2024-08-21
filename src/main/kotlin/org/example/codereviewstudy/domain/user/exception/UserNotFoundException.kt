package org.example.codereviewstudy.domain.user.exception

import org.example.codereviewstudy.common.exception.DuplicatedException

class UserNotFoundException(
    val username: String,
    override val message: String = "사용자를 찾을 수 없습니다.",
) : DuplicatedException(
    message = message,
    duplicateValue = username,
)