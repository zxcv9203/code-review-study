package org.example.codereviewstudy.domain.post.exception

import org.example.codereviewstudy.common.exception.NotMatchedException
import org.example.codereviewstudy.domain.post.exception.model.PostErrorMessage
import org.springframework.http.HttpStatus

class PostAuthorNotMatchedException(
    val authorId: Long,
    val loginUserId: Long,
    override val message: String = PostErrorMessage.AUTHOR_NOT_MATCHED.message,
) :
    NotMatchedException(
        status = HttpStatus.FORBIDDEN,
        message = message,
        expectedValue = authorId,
        actualValue = loginUserId,
    ) {
    override val errorMessage: String
        get() = "$message : 작성자 ID: $authorId, 로그인 사용자 ID: $loginUserId"
}