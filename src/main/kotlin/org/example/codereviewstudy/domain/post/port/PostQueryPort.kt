package org.example.codereviewstudy.domain.post.port

import org.example.codereviewstudy.common.model.Page
import org.example.codereviewstudy.domain.post.model.Post
import org.example.codereviewstudy.infrastructure.web.rest.post.request.PostQueryRequest

interface PostQueryPort {
    fun findById(id: Long): Post
    fun findAllByCursor(request: PostQueryRequest): Page<Post>
}