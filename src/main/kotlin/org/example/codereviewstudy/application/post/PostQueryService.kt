package org.example.codereviewstudy.application.post

import org.example.codereviewstudy.domain.post.model.toResponse
import org.example.codereviewstudy.domain.post.port.PostQueryPort
import org.example.codereviewstudy.infrastructure.web.rest.post.response.PostResponse
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
}


