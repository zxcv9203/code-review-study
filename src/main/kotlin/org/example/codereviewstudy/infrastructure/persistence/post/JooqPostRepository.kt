package org.example.codereviewstudy.infrastructure.persistence.post

import org.example.codereviewstudy.domain.post.model.Post
import org.example.codereviewstudy.domain.post.port.PostQueryPort
import org.jooq.DSLContext
import org.springframework.data.domain.Page
import org.springframework.stereotype.Repository

@Repository
class JooqPostRepository(
    private val query: DSLContext
): PostQueryPort {
    override fun findById(id: Long): Post {
        TODO("Not yet implemented")
    }

    override fun findAllByCursor(id: Long, size: Int): Page<Post> {
        TODO("Not yet implemented")
    }
}