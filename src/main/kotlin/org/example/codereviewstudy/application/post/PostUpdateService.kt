package org.example.codereviewstudy.application.post

import org.example.codereviewstudy.application.user.UserQueryService
import org.example.codereviewstudy.domain.post.exception.PostAuthorNotMatchedException
import org.example.codereviewstudy.domain.post.model.toUpdateResponse
import org.example.codereviewstudy.domain.post.port.PostQueryPort
import org.example.codereviewstudy.domain.post.port.PostUpdatePort
import org.example.codereviewstudy.infrastructure.web.rest.post.request.PostUpdateRequest
import org.example.codereviewstudy.infrastructure.web.rest.post.response.PostUpdateResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PostUpdateService(
    private val userQueryService: UserQueryService,
    private val postQueryPort: PostQueryPort,
    private val postUpdatePort: PostUpdatePort,
) {
    fun update(id: Long, request: PostUpdateRequest, userId: Long): PostUpdateResponse {
        userQueryService.findById(userId)

        return postQueryPort.findById(id)
            .also { checkPostAuthor(it.author.id, userId) }
            .apply { update(request.title, request.content) }
            .let { postUpdatePort.update(it) }
            .toUpdateResponse()
    }

    private fun checkPostAuthor(authorId: Long, userId: Long) {
        if (authorId != userId) {
            throw PostAuthorNotMatchedException(authorId, userId)
        }
    }
}