package org.example.codereviewstudy.domain.user.model

import org.example.codereviewstudy.infrastructure.persistence.user.UserJpaEntity

class User(
    var username: String,

    var password: String,

    val id: Long? = null,
)

fun User.toJpaEntity(): UserJpaEntity {
    return UserJpaEntity(
        username = this.username,
        password = this.password,
    )
}