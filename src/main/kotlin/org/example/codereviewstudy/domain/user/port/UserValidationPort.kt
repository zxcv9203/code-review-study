package org.example.codereviewstudy.domain.user.port

interface UserValidationPort {
    fun isDuplicatedUsername(username: String): Boolean
}