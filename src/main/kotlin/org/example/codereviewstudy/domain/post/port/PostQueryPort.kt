package org.example.codereviewstudy.domain.post.port

import org.example.codereviewstudy.domain.post.model.Post
import org.springframework.data.domain.Page

interface PostQueryPort {
    fun findById(id: Long): Post
    fun findAllByCursor(id: Long, size: Int): Page<Post>
}