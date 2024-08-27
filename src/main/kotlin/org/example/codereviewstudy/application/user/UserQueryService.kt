package org.example.codereviewstudy.application.user

import org.example.codereviewstudy.domain.user.exception.UserNotFoundException
import org.example.codereviewstudy.domain.user.port.UserQueryPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserQueryService(
    private val userQueryPort: UserQueryPort
) {
    fun findById(id: Long) = userQueryPort.findById(id)
        ?: throw UserNotFoundException(id)
}