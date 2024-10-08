package org.example.codereviewstudy.infrastructure.persistence.user

import jakarta.persistence.*
import org.example.codereviewstudy.infrastructure.persistence.model.BaseTimeEntity
import org.example.codereviewstudy.domain.user.model.User

@Entity
@Table(name = "users", indexes = [Index(name = "idx_username", columnList = "username")])
class UserJpaEntity(
    @Column(name = "username", nullable = false, length = 20)
    var username: String,

    @Column(name = "password", nullable = false, length = 255)
    var password: String,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
) : BaseTimeEntity()