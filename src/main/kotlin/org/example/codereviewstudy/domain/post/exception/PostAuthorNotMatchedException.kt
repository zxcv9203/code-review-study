package org.example.codereviewstudy.domain.post.exception

import org.example.codereviewstudy.common.exception.NotMatchedException
import org.springframework.http.HttpStatus

class PostAuthorNotMatchedException(
    val authorId: Long,
    val loginUserId: Long,
    override val message: String = "내가 작성한 게시글이 아닙니다.",
):
NotMatchedException(
    status = HttpStatus.FORBIDDEN,
    message = message,
    expectedValue = authorId,
    actualValue = loginUserId,
){
    override val errorMessage: String
        get() = "$message : 작성자 ID: $authorId, 로그인 사용자 ID: $loginUserId"
}