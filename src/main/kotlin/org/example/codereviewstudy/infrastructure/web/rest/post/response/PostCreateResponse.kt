package org.example.codereviewstudy.infrastructure.web.rest.post.response

import java.time.LocalDateTime

data class PostCreateResponse(
    val title: String,
    val author: String,
    val content: String,
    val createdAt: LocalDateTime,
)