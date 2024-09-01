package org.example.codereviewstudy.infrastructure.web.rest.post.response

data class PostResponses(
    val isLast: Boolean,
    val posts: List<PostResponse>
)