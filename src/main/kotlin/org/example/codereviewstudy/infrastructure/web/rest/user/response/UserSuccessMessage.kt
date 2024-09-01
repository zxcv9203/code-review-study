package org.example.codereviewstudy.infrastructure.web.rest.user.response

enum class UserSuccessMessage(
    val message: String,
) {
    SIGNUP("회원가입에 성공했습니다."),
    LOGIN("로그인에 성공했습니다."),
}