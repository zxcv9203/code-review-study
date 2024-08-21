package org.example.codereviewstudy.domain.user.port

import org.example.codereviewstudy.domain.user.model.User

interface UserQueryPort {
    fun findByUsername(username: String): User
}