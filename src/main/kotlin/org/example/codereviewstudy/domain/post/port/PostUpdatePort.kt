package org.example.codereviewstudy.domain.post.port

import org.example.codereviewstudy.domain.post.model.Post

interface PostUpdatePort {
    fun update(post: Post): Post
}