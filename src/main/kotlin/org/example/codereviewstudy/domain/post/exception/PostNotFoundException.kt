package org.example.codereviewstudy.domain.post.exception

import org.example.codereviewstudy.common.exception.NotFoundException

class PostNotFoundException(
    val id: Long,
    override val message: String = "해당 게시글을 찾을 수 없습니다."
): NotFoundException(
    message = message,
    notFoundValue = id
){
    override val errorMessage: String
        get() = "$message 게시글 ID: $id"
}