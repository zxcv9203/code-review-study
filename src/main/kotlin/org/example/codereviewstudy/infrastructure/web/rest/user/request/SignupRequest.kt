package org.example.codereviewstudy.infrastructure.web.rest.user.request

import jakarta.validation.constraints.Pattern

data class SignupRequest(

    @field:Pattern(regexp = "^[a-z0-9]{4,10}$", message = "아이디는 4자 이상 10자 이하의 알파벳 소문자와 숫자만 허용됩니다.")
    val username: String,

    @field:Pattern(regexp = "^[a-zA-Z0-9]{8,15}$", message = "비밀번호는 8자 이상 15자 이하의 알파벳 대소문자와 숫자만 허용됩니다.")
    val password: String,
)
