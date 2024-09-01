package org.example.codereviewstudy.infrastructure.web.rest.post

import org.example.codereviewstudy.application.post.PostDeleteService
import org.example.codereviewstudy.common.model.ApiResponse
import org.example.codereviewstudy.infrastructure.auth.model.AuthUser
import org.example.codereviewstudy.infrastructure.web.rest.post.response.PostSuccessMessage
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/posts")
class PostDeleteController(
    private val postDeleteService: PostDeleteService
) {

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: Long,
        authUser: AuthUser,
    ): ResponseEntity<ApiResponse<Unit>> {
        return postDeleteService.delete(id, authUser.id)
            .let { ApiResponse.success(HttpStatus.OK, PostSuccessMessage.DELETED.message) }
            .let { ResponseEntity.status(HttpStatus.OK).body(it) }
    }
}