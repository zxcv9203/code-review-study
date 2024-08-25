package org.example.codereviewstudy.infrastructure.persistence.user

import org.springframework.data.jpa.repository.JpaRepository

interface JpaUserRepository: JpaRepository<UserJpaEntity, Long> {

    fun existsByUsername(username: String): Boolean

    fun findByUsername(username: String): UserJpaEntity?
}