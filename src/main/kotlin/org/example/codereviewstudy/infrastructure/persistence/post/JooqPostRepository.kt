package org.example.codereviewstudy.infrastructure.persistence.post

import org.example.codereviewstudy.common.model.page.Page
import org.example.codereviewstudy.common.model.page.Sort
import org.example.codereviewstudy.domain.post.exception.PostNotFoundException
import org.example.codereviewstudy.domain.post.model.Post
import org.example.codereviewstudy.domain.post.port.PostQueryPort
import org.example.codereviewstudy.infrastructure.persistence.post.mapper.toPost
import org.example.codereviewstudy.infrastructure.web.rest.post.request.PostQueryRequest
import org.example.codereviewstudy.public_.tables.Posts.POSTS
import org.example.codereviewstudy.public_.tables.Users.USERS
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortField
import org.jooq.impl.DSL
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
            .where(buildIdFilterCondition(request))
            .orderBy(buildIdSortOrder(request))
            .limit(request.size + 1) // size보다 1개 더 조회해서 다음 페이지 여부를 판단하기 위함
            .fetch()

        return posts.subList(0, minOf(posts.size, request.size))
            .map { it.toPost() }
            .let { Page(it, posts.size <= request.size) }
    }

    private fun buildIdFilterCondition(request: PostQueryRequest): Condition {
        return if (request.id == null) {
            DSL.noCondition()
        } else {
            when (request.sortOrder) {
                Sort.ASC -> POSTS.ID.gt(request.id)
                Sort.DESC -> POSTS.ID.lt(request.id)
            }
        }
    }

    fun buildIdSortOrder(request: PostQueryRequest): SortField<Long> {
        return when (request.sortOrder) {
            Sort.ASC -> POSTS.ID.asc()
            Sort.DESC -> POSTS.ID.desc()
        }
    }
}