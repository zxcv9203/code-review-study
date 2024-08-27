package org.example.codereviewstudy.domain.user.exception

import org.example.codereviewstudy.common.exception.NotFoundException

class UserNotFoundException(
    val id: Long,
    override val message: String = "사용자를 찾을 수 없습니다."
): NotFoundException(
    message = message,
    notFoundValue = id
) {
    override val errorMessage: String
        get() = "$message 사용자 ID: $id"
}