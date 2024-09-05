package org.example.codereviewstudy.infrastructure.persistence.post

import org.example.codereviewstudy.common.model.Page
import org.example.codereviewstudy.domain.post.exception.PostNotFoundException
import org.example.codereviewstudy.domain.post.model.Post
import org.example.codereviewstudy.domain.post.port.PostCreatePort
import org.example.codereviewstudy.domain.post.port.PostQueryPort
import org.example.codereviewstudy.domain.post.port.PostUpdatePort
import org.example.codereviewstudy.domain.user.port.PostDeletePort
import org.example.codereviewstudy.infrastructure.persistence.post.mapper.toDomain
import org.example.codereviewstudy.infrastructure.persistence.post.mapper.toJpaEntity
import org.example.codereviewstudy.infrastructure.web.rest.post.request.PostQueryRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class JpaPostRepository(
    private val springDataJpaPostRepository: SpringDataJpaPostRepository,
) : PostCreatePort, PostQueryPort, PostUpdatePort, PostDeletePort {

    override fun create(post: Post): Post {
        return springDataJpaPostRepository.save(post.toJpaEntity())
            .toDomain()
    }

    override fun findById(id: Long): Post {
        return springDataJpaPostRepository.findByIdOrNull(id)
            ?.toDomain()
            ?: throw PostNotFoundException(id)
    }

    override fun findAllByCursor(request: PostQueryRequest): Page<Post> {
        TODO("Not yet implemented")
    }

    override fun update(post: Post): Post {
        val u = springDataJpaPostRepository.save(post.toJpaEntity())
        return u.toDomain()
    }

    override fun delete(post: Post) {
        springDataJpaPostRepository.delete(post.toJpaEntity())
    }
}



