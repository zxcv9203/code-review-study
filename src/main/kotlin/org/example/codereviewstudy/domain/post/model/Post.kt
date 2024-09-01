package org.example.codereviewstudy.domain.post.model

import org.example.codereviewstudy.domain.user.model.User
import org.example.codereviewstudy.domain.user.model.toJpaEntity
import org.example.codereviewstudy.infrastructure.persistence.post.PostJpaEntity
import org.example.codereviewstudy.infrastructure.web.rest.post.response.PostCreateResponse
import org.example.codereviewstudy.infrastructure.web.rest.post.response.PostResponse
import org.example.codereviewstudy.infrastructure.web.rest.post.response.PostUpdateResponse
import java.time.LocalDateTime

class Post(
    var title: String,
    var content: String,
    val author: User,
    val createdAt: LocalDateTime,

    val id: Long = 0,
) {
    fun update(title: String, content: String) {
        this.title = title
        this.content = content
    }
}

fun Post.toJpaEntity(): PostJpaEntity {
    return PostJpaEntity(
        author = author.toJpaEntity(),
        title = title,
        content = content,
        id = id,
    )
}

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