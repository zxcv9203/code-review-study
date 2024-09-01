package org.example.codereviewstudy.application.post

import org.example.codereviewstudy.domain.post.port.PostQueryPort
import org.example.codereviewstudy.infrastructure.web.rest.post.mapper.toResponse
import org.example.codereviewstudy.infrastructure.web.rest.post.response.PostResponse
import org.example.codereviewstudy.infrastructure.web.rest.post.response.PostResponses
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PostQueryService(
    private val postQueryPort: PostQueryPort
) {
    fun findById(id: Long): PostResponse {
        return postQueryPort.findById(id)
            .toResponse()
    }

    fun findAll(pageable: Pageable): PostResponses {
        return postQueryPort.findAll(pageable)
            .map { it.toResponse() }
            .let { PostResponses(it.isLast, it.content) }
    }
}


