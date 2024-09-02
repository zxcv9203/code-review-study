package org.example.codereviewstudy.application.post

import org.example.codereviewstudy.application.user.UserQueryService
import org.example.codereviewstudy.domain.post.exception.PostAuthorNotMatchedException
import org.example.codereviewstudy.domain.post.port.PostQueryPort
import org.example.codereviewstudy.domain.user.port.PostDeletePort
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class PostDeleteService(
    private val userQueryService: UserQueryService,

    @Qualifier("jpaPostRepository")
    private val postQueryPort: PostQueryPort,
    private val postDeletePort: PostDeletePort,
) {

    fun delete(id: Long, userId: Long) {
        userQueryService.findById(userId)

        postQueryPort.findById(id)
            .also { checkPostAuthor(it.author.id, userId) }
            .let { postDeletePort.delete(it) }
    }

    private fun checkPostAuthor(authorId: Long, userId: Long) {
        if (authorId != userId) {
            throw PostAuthorNotMatchedException(authorId, userId)
        }
    }
}