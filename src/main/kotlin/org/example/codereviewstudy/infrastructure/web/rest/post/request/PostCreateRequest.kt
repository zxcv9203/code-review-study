package org.example.codereviewstudy.infrastructure.web.rest.post.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.example.codereviewstudy.domain.post.model.Post
import org.example.codereviewstudy.domain.user.model.User
import java.time.LocalDateTime

data class PostCreateRequest(
    @field:NotBlank(message = "게시글 제목을 입력해주세요.")
    @field:Size(max = 255, message = "게시글 제목은 255자 이하로 입력해주세요.")
    val title: String,

    @field:NotBlank(message = "게시글 본문 내용을 입력해주세요.")
    val content: String,
)

fun PostCreateRequest.toDomain(author: User): Post {
    return Post(
        title = title,
        content = content,
        author = author,
        createdAt = LocalDateTime.now(),
    )
}