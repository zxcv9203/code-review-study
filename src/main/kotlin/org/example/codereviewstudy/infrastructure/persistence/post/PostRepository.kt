package org.example.codereviewstudy.infrastructure.persistence.post

import org.example.codereviewstudy.domain.post.model.Post
import org.example.codereviewstudy.domain.post.model.toJpaEntity
import org.example.codereviewstudy.domain.post.port.PostCreatePort
import org.springframework.stereotype.Repository

@Repository
class PostRepository(
    private val jpaPostRepository: JpaPostRepository,
): PostCreatePort {

    override fun create(post: Post): Post {
        return jpaPostRepository.save(post.toJpaEntity())
            .toDomain()
    }
}



