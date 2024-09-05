package org.example.codereviewstudy.infrastructure.web.rest.post.request

data class PostQueryRequest(
    val id: Long? = null,
    val size: Int = 10,
    val sortOrder: String = "desc"
)
