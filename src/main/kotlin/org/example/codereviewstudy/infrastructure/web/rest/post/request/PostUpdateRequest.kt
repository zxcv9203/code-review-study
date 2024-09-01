package org.example.codereviewstudy.infrastructure.web.rest.post.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class PostUpdateRequest(
    @field:NotBlank(message = "게시글 제목을 입력해주세요.")
    @field:Size(max = 255, message = "게시글 제목은 255자 이하로 입력해주세요.")
    val title: String,

    @field:NotBlank(message = "게시글 본문 내용을 입력해주세요.")
    val content: String,
) {
}