package org.example.codereviewstudy.infrastructure.persistence.post

import org.example.codereviewstudy.domain.post.exception.PostNotFoundException
import org.example.codereviewstudy.domain.post.model.Post
import org.example.codereviewstudy.domain.post.model.toJpaEntity
import org.example.codereviewstudy.domain.post.port.PostCreatePort
import org.example.codereviewstudy.domain.post.port.PostQueryPort
import org.example.codereviewstudy.domain.post.port.PostUpdatePort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class PostRepository(
    private val jpaPostRepository: JpaPostRepository,
) : PostCreatePort, PostQueryPort, PostUpdatePort {

    override fun create(post: Post): Post {
        return jpaPostRepository.save(post.toJpaEntity())
            .toDomain()
    }

    override fun findById(id: Long): Post {
        return jpaPostRepository.findByIdOrNull(id)
            ?.toDomain()
            ?: throw PostNotFoundException(id)
    }

    override fun update(post: Post): Post {
        val u = jpaPostRepository.save(post.toJpaEntity())
        return u.toDomain()
    }
}



