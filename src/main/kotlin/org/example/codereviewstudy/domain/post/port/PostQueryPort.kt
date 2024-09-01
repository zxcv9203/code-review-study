package org.example.codereviewstudy.domain.post.port

import org.example.codereviewstudy.domain.post.model.Post
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface PostQueryPort {
    fun findById(id: Long): Post
    fun findAll(pageable: Pageable): Page<Post>
}