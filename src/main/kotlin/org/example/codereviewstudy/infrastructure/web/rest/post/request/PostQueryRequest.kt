package org.example.codereviewstudy.infrastructure.web.rest.post.request

data class PostQueryRequest(
    val id: Long = 1,
    val size: Int = 10,
    val sort: String,
    val orderBy: String,
)
