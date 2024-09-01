package org.example.codereviewstudy.domain.post.exception

import org.example.codereviewstudy.common.exception.NotFoundException
import org.example.codereviewstudy.domain.post.exception.model.PostErrorMessage

class PostNotFoundException(
    val id: Long,
    override val message: String = PostErrorMessage.NOT_FOUND.message
): NotFoundException(
    message = message,
    notFoundValue = id
){
    override val errorMessage: String
        get() = "$message 게시글 ID: $id"
}