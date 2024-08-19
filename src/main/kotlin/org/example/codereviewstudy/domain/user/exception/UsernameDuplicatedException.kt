package org.example.codereviewstudy.domain.user.exception

import org.example.codereviewstudy.common.exception.DuplicatedException

class UsernameDuplicatedException(
    val username: String,
    override val message: String = "이미 존재하는 사용자 이름입니다.",
) : DuplicatedException(
    message = message,
    duplicateValue = username,
)