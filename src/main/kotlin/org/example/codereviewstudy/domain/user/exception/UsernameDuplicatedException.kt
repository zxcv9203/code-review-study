package org.example.codereviewstudy.domain.user.exception

import org.example.codereviewstudy.common.exception.DuplicatedException
import org.example.codereviewstudy.domain.user.exception.model.UserErrorMessage

class UsernameDuplicatedException(
    val username: String,
    override val message: String = UserErrorMessage.DUPLICATED_USERNAME.message,
) : DuplicatedException(
    message = message,
    duplicateValue = username,
)