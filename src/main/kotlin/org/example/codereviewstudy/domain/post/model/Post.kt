package org.example.codereviewstudy.domain.post.model

import org.example.codereviewstudy.domain.user.model.User
import java.time.LocalDateTime

class Post(
    var title: String,
    var content: String,
    val author: User,
    val createdAt: LocalDateTime,

    val id: Long = 0,
) {
    fun update(title: String, content: String) {
        this.title = title
        this.content = content
    }
}