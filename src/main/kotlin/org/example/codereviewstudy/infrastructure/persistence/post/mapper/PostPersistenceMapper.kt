package org.example.codereviewstudy.infrastructure.persistence.post.mapper

import org.example.codereviewstudy.domain.post.model.Post
import org.example.codereviewstudy.infrastructure.persistence.post.PostJpaEntity
import org.example.codereviewstudy.infrastructure.persistence.user.mapper.toDomain
import org.example.codereviewstudy.infrastructure.persistence.user.mapper.toJpaEntity

fun Post.toJpaEntity(): PostJpaEntity {
    return PostJpaEntity(
        author = author.toJpaEntity(),
        title = title,
        content = content,
        id = id,
    )
}

fun PostJpaEntity.toDomain(): Post {
    return Post(
        author = author.toDomain(),
        title = title,
        content = content,
        createdAt = createdAt,
        id = id,
    )
}

