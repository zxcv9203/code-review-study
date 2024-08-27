package org.example.codereviewstudy.application.post

import org.example.codereviewstudy.application.user.UserQueryService
import org.example.codereviewstudy.domain.post.model.toCreateResponse
import org.example.codereviewstudy.domain.post.port.PostCreatePort
import org.example.codereviewstudy.infrastructure.auth.model.AuthUser
import org.example.codereviewstudy.infrastructure.web.rest.post.request.PostCreateRequest
import org.example.codereviewstudy.infrastructure.web.rest.post.request.toDomain
import org.example.codereviewstudy.infrastructure.web.rest.post.response.PostCreateResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PostCreateService(
    private val userQueryService: UserQueryService,
    private val postCreatePort: PostCreatePort,
) {
    fun create(request: PostCreateRequest, authUser: AuthUser): PostCreateResponse {
        val author = userQueryService.findById(authUser.id)
        val post = request.toDomain(author)

        return postCreatePort.create(post)
            .toCreateResponse()
    }

}