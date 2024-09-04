package org.example.codereviewstudy.infrastructure.persistence.post.mapper

import org.example.codereviewstudy.domain.post.model.Post
import org.example.codereviewstudy.domain.user.model.User
import org.example.codereviewstudy.infrastructure.persistence.post.PostJpaEntity
import org.example.codereviewstudy.infrastructure.persistence.user.mapper.toDomain
import org.example.codereviewstudy.infrastructure.persistence.user.mapper.toJpaEntity
import org.example.codereviewstudy.public_.tables.Posts.POSTS
import org.example.codereviewstudy.public_.tables.Users.USERS
import org.jooq.Record

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

fun Record.toPost(): Post {
    return Post(
        id = this[POSTS.ID],
        title = this[POSTS.TITLE],
        content = this[POSTS.CONTENT],
        createdAt = this[POSTS.CREATED_AT],
        author = USERS.let {
            User(
                id = this[it.ID],
                username = this[it.USERNAME],
                password = this[it.PASSWORD]
            )
        }
    )
}