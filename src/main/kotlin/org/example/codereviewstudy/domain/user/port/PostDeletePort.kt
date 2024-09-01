package org.example.codereviewstudy.domain.user.port

import org.example.codereviewstudy.domain.post.model.Post

interface PostDeletePort {
    fun delete(post: Post)
}