package org.example.codereviewstudy.infrastructure.web.rest.post

import jakarta.validation.Valid
import org.example.codereviewstudy.application.post.PostCreateService
import org.example.codereviewstudy.common.model.ApiResponse
import org.example.codereviewstudy.infrastructure.auth.model.AuthUser
import org.example.codereviewstudy.infrastructure.web.rest.post.request.PostCreateRequest
import org.example.codereviewstudy.infrastructure.web.rest.post.response.PostCreateResponse
import org.example.codereviewstudy.infrastructure.web.rest.post.response.PostSuccessMessage
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/posts")
class PostCreateController(
    private val postCreateService: PostCreateService
) {

    @PostMapping
    fun create(
        @RequestBody @Valid request: PostCreateRequest,
        authUser: AuthUser,
    ): ResponseEntity<ApiResponse<PostCreateResponse>> {
        return postCreateService.create(request, authUser)
            .let { ApiResponse.success(HttpStatus.CREATED, PostSuccessMessage.CREATED.message, it) }
            .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }
    }
}