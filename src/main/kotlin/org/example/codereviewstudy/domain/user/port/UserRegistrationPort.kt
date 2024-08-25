package org.example.codereviewstudy.domain.user.port

import org.example.codereviewstudy.domain.user.model.User

interface UserRegistrationPort {
    fun signup(user: User): User
}