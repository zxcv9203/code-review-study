package org.example.codereviewstudy.infrastructure.persistence.post

import org.springframework.data.jpa.repository.JpaRepository

interface SpringDataJpaPostRepository : JpaRepository<PostJpaEntity, Long>