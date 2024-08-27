package org.example.codereviewstudy.domain.post.port

import org.example.codereviewstudy.domain.post.model.Post

interface PostCreatePort {
    fun create(post: Post): Post
}