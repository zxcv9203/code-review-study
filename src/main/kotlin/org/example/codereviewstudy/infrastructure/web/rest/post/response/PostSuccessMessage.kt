package org.example.codereviewstudy.infrastructure.web.rest.post.response

enum class PostSuccessMessage(
    val message: String
) {
    CREATED("게시글 작성에 성공했습니다."),
    DELETED("게시글 삭제에 성공했습니다."),
    GET("게시글 조회에 성공했습니다."),
    LIST_GET("게시글 목록 조회에 성공했습니다."),
    UPDATED("게시글 수정에 성공했습니다."),
}