package org.example.codereviewstudy.common.exception.message

enum class ErrorMessage(
    val message: String
) {
    // 400
    VALIDATION_FAILED("잘못된 요청입니다."),

    // 401
    AUTHENTICATION_FAILED("사용자 이름 혹은 비밀번호를 다시 확인해주세요."),

    // 404
    NOT_FOUND("요청한 리소스를 찾을 수 없습니다."),

    // 500
    INTERNAL_SERVER_ERROR("서버 에러가 발생했습니다.")
}