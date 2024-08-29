package org.example.codereviewstudy.infrastructure.web.rest.post

import org.example.codereviewstudy.application.post.PostQueryService
import org.example.codereviewstudy.common.model.ApiResponse
import org.example.codereviewstudy.infrastructure.auth.model.AuthUser
import org.example.codereviewstudy.infrastructure.web.rest.post.response.PostResponse
import org.example.codereviewstudy.infrastructure.web.rest.post.response.PostResponses
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/posts")
class PostQueryController(
    private val postQueryService: PostQueryService
) {

    @GetMapping("/{id}")
    fun findById(
        @PathVariable id: Long,
        authUser: AuthUser
    ): ResponseEntity<ApiResponse<PostResponse>> {
        return postQueryService.findById(id)
            .let { ApiResponse.success(HttpStatus.OK, "게시글 조회에 성공했습니다.", it) }
            .let { ResponseEntity.status(HttpStatus.OK).body(it) }
    }

    @GetMapping
    fun findAll(
        pageable: Pageable,
        authUser: AuthUser
    ): ResponseEntity<ApiResponse<PostResponses>> {
        return postQueryService.findAll(pageable)
            .let { ApiResponse.success(HttpStatus.OK, "게시글 목록 조회에 성공했습니다.", it) }
            .let { ResponseEntity.status(HttpStatus.OK).body(it) }
    }
}