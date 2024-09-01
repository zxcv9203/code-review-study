package org.example.codereviewstudy.domain.user.exception

import org.example.codereviewstudy.common.exception.NotFoundException
import org.example.codereviewstudy.domain.user.exception.model.UserErrorMessage

class UserNotFoundException(
    val id: Long,
    override val message: String = UserErrorMessage.NOT_FOUND.message
): NotFoundException(
    message = message,
    notFoundValue = id
) {
    override val errorMessage: String
        get() = "$message 사용자 ID: $id"
}