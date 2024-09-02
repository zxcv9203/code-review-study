package org.example.codereviewstudy.infrastructure.web.rest.post.mapper

import org.example.codereviewstudy.domain.post.model.Post
import org.example.codereviewstudy.domain.user.model.User
import org.example.codereviewstudy.infrastructure.web.rest.post.request.PostCreateRequest
import org.example.codereviewstudy.infrastructure.web.rest.post.response.PostCreateResponse
import org.example.codereviewstudy.infrastructure.web.rest.post.response.PostResponse
import org.example.codereviewstudy.infrastructure.web.rest.post.response.PostUpdateResponse

import java.time.LocalDateTime

fun Post.toCreateResponse(): PostCreateResponse {
    return PostCreateResponse(
        title = title,
        author = author.username,
        content = content,
        createdAt = createdAt,
        id = id,
    )
}

fun Post.toResponse(): PostResponse {
    return PostResponse(
        title = title,
        author = author.username,
        content = content,
        createdAt = createdAt,
        id = id,
    )
}

fun Post.toUpdateResponse(): PostUpdateResponse {
    return PostUpdateResponse(
        title = title,
        author = author.username,
        content = content,
        createdAt = createdAt,
        id = id,
    )
}

fun PostCreateRequest.toDomain(author: User): Post {
    return Post(
        title = title,
        content = content,
        author = author,
        createdAt = LocalDateTime.now(),
    )
}