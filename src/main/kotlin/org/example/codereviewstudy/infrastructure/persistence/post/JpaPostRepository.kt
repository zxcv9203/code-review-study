package org.example.codereviewstudy.infrastructure.persistence.post

import org.springframework.data.jpa.repository.JpaRepository

interface JpaPostRepository : JpaRepository<PostJpaEntity, Long>