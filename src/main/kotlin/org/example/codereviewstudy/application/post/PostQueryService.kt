package org.example.codereviewstudy.application.post

import org.example.codereviewstudy.domain.post.port.PostQueryPort
import org.example.codereviewstudy.infrastructure.web.rest.post.mapper.toResponse
import org.example.codereviewstudy.infrastructure.web.rest.post.request.PostQueryRequest
import org.example.codereviewstudy.infrastructure.web.rest.post.response.PostResponse
import org.example.codereviewstudy.infrastructure.web.rest.post.response.PostResponses
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PostQueryService(
    @Qualifier("jooqPostRepository") private val postQueryPort: PostQueryPort
) {
    fun findById(id: Long): PostResponse {
        return postQueryPort.findById(id)
            .toResponse()
    }

    fun findAll(request: PostQueryRequest): PostResponses {
        return postQueryPort.findAllByCursor(request.id, request.size)
            .map { it.toResponse() }
            .let { PostResponses(it.isLast, it.content) }
    }
}


