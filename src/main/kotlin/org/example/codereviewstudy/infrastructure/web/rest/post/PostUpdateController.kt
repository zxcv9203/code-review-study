package org.example.codereviewstudy.infrastructure.web.rest.post

import jakarta.validation.Valid
import org.example.codereviewstudy.application.post.PostUpdateService
import org.example.codereviewstudy.common.model.ApiResponse
import org.example.codereviewstudy.infrastructure.auth.model.AuthUser
import org.example.codereviewstudy.infrastructure.web.rest.post.request.PostUpdateRequest
import org.example.codereviewstudy.infrastructure.web.rest.post.response.PostUpdateResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/posts")
class PostUpdateController(
    private val postUpdateService: PostUpdateService
) {

    @PatchMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody @Valid request: PostUpdateRequest,
        authUser: AuthUser,
    ): ResponseEntity<ApiResponse<PostUpdateResponse>> {
        return postUpdateService.update(id, request, authUser.id)
            .let { ApiResponse.success(HttpStatus.OK, "게시글 수정에 성공했습니다.", it) }
            .let { ResponseEntity.status(HttpStatus.OK).body(it) }
    }

}