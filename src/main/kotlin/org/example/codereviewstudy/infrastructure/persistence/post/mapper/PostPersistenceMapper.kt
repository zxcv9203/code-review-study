package org.example.codereviewstudy.infrastructure.persistence.post.mapper

import org.example.codereviewstudy.domain.post.model.Post
import org.example.codereviewstudy.domain.user.model.toJpaEntity
import org.example.codereviewstudy.infrastructure.persistence.post.PostJpaEntity

fun Post.toJpaEntity(): PostJpaEntity {
    return PostJpaEntity(
        author = author.toJpaEntity(),
        title = title,
        content = content,
        id = id,
    )
}

