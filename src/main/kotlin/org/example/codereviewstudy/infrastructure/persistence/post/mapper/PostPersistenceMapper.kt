package org.example.codereviewstudy.infrastructure.persistence.post.mapper

import org.example.codereviewstudy.domain.post.model.Post
import org.example.codereviewstudy.infrastructure.persistence.post.PostJpaEntity
import org.example.codereviewstudy.infrastructure.persistence.user.mapper.toJpaEntity

fun Post.toJpaEntity(): PostJpaEntity {
    return PostJpaEntity(
        author = author.toJpaEntity(),
        title = title,
        content = content,
        id = id,
    )
}

