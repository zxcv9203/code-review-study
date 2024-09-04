package org.example.codereviewstudy.infrastructure.persistence.post

import org.example.codereviewstudy.domain.post.exception.PostNotFoundException
import org.example.codereviewstudy.domain.post.model.Post
import org.example.codereviewstudy.domain.post.port.PostQueryPort
import org.example.codereviewstudy.infrastructure.persistence.post.mapper.toPost
import org.example.codereviewstudy.public_.tables.Posts.POSTS
import org.example.codereviewstudy.public_.tables.Users.USERS
import org.jooq.DSLContext
import org.springframework.data.domain.Page
import org.springframework.stereotype.Repository

@Repository
class JooqPostRepository(
    private val query: DSLContext
) : PostQueryPort {
    override fun findById(id: Long): Post {
        val post = query.select()
            .from(POSTS)
            .join(USERS).on(POSTS.AUTHOR_ID.eq(USERS.ID))
            .where(POSTS.ID.eq(id))
            .fetchOne()

        return post?.toPost()
            ?: throw PostNotFoundException(id)
    }

    override fun findAllByCursor(id: Long, size: Int): Page<Post> {
        TODO("Not yet implemented")
    }
}