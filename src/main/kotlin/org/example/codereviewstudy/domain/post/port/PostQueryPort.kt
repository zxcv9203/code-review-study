package org.example.codereviewstudy.domain.post.port

import org.example.codereviewstudy.domain.post.model.Post
import org.example.codereviewstudy.infrastructure.web.rest.post.response.PostResponse

interface PostQueryPort {
    fun findById(id: Long): Post
}