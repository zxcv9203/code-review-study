package org.example.codereviewstudy.domain.post.model

import org.example.codereviewstudy.domain.user.model.User
import org.example.codereviewstudy.domain.user.model.toJpaEntity
import org.example.codereviewstudy.infrastructure.persistence.post.PostJpaEntity
import org.example.codereviewstudy.infrastructure.web.rest.post.response.PostCreateResponse
import java.time.LocalDateTime

class Post(
    var title: String,
    var content: String,
    val author: User,
    val createdAt: LocalDateTime,

    val id: Long = 0,
)

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
    )
}