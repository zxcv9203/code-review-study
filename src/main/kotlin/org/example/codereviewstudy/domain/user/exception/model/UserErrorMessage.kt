package org.example.codereviewstudy.domain.user.exception.model

enum class UserErrorMessage(
    val message: String,
) {
    DUPLICATED_USERNAME("이미 존재하는 사용자 이름입니다."),
    NOT_FOUND("사용자를 찾을 수 없습니다."),
}