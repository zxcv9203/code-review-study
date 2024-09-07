package org.example.codereviewstudy.infrastructure.persistence.user.mapper

import org.example.codereviewstudy.domain.user.model.User
import org.example.codereviewstudy.infrastructure.persistence.user.UserJpaEntity

fun UserJpaEntity.toDomain(): User {
    return User(
        username = this.username,
        password = this.password,
        id = this.id,
    )
}

fun User.toJpaEntity(): UserJpaEntity {
    return UserJpaEntity(
        username = this.username,
        password = this.password,
        id = this.id,
    )
}