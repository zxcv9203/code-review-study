package org.example.codereviewstudy.domain.post.exception.model

enum class PostErrorMessage(
    val message: String,
) {
    AUTHOR_NOT_MATCHED("내가 작성한 게시글이 아닙니다."),
    NOT_FOUND("해당 게시글을 찾을 수 없습니다.")
}