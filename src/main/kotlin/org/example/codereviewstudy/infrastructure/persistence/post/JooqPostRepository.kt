package org.example.codereviewstudy.infrastructure.persistence.post

import org.example.codereviewstudy.common.model.Page
import org.example.codereviewstudy.domain.post.exception.PostNotFoundException
import org.example.codereviewstudy.domain.post.model.Post
import org.example.codereviewstudy.domain.post.port.PostQueryPort
import org.example.codereviewstudy.infrastructure.persistence.post.mapper.toPost
import org.example.codereviewstudy.infrastructure.web.rest.post.request.PostQueryRequest
import org.example.codereviewstudy.public_.tables.Posts.POSTS
import org.example.codereviewstudy.public_.tables.Users.USERS
import org.jooq.DSLContext
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

    override fun findAllByCursor(request: PostQueryRequest): Page<Post> {
        val posts = query.select()
            .from(POSTS)
            .join(USERS).on(POSTS.AUTHOR_ID.eq(USERS.ID))

        when {
            request.id == null -> {
                when (request.sortOrder) {
                    "asc" -> posts.orderBy(POSTS.ID.asc())
                    else -> posts.orderBy(POSTS.ID.desc())
                }
            }
            request.sortOrder == "asc" -> {
                posts.where(POSTS.ID.gt(request.id))
                    .orderBy(POSTS.ID.asc())
            }
            else -> {
                posts.where(POSTS.ID.lt(request.id))
                    .orderBy(POSTS.ID.desc())
            }
        }

        // size보다 1개 더 조회해서 다음 페이지 여부를 판단하기 위함
        val result = posts.limit(request.size + 1)
            .fetch()

        return result.subList(0, minOf(result.size, request.size))
            .map { it.toPost() }
            .let { Page(it, result.size <= request.size) }
    }
}