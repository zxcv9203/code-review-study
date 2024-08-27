package org.example.codereviewstudy.infrastructure.persistence.user

import org.example.codereviewstudy.domain.user.model.User
import org.example.codereviewstudy.domain.user.model.toJpaEntity
import org.example.codereviewstudy.domain.user.port.UserQueryPort
import org.example.codereviewstudy.domain.user.port.UserRegistrationPort
import org.example.codereviewstudy.domain.user.port.UserValidationPort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class UserRepository(
    private val jpaUserRepository: JpaUserRepository
) : UserValidationPort, UserRegistrationPort, UserQueryPort {
    override fun isDuplicatedUsername(username: String): Boolean {
        return jpaUserRepository.existsByUsername(username)
    }

    override fun signup(user: User): User {
        return jpaUserRepository.save(user.toJpaEntity())
            .toUser()
    }

    override fun findByUsername(username: String): User? {
        return jpaUserRepository.findByUsername(username)
            ?.toUser()
    }

    override fun findById(id: Long): User? {
        return jpaUserRepository.findByIdOrNull(id)
            ?.toUser()
    }
}
