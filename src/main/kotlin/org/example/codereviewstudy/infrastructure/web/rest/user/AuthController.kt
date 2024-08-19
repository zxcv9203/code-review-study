package org.example.codereviewstudy.infrastructure.web.rest.user

import jakarta.validation.Valid
import org.example.codereviewstudy.application.user.AuthService
import org.example.codereviewstudy.common.model.ApiResponse
import org.example.codereviewstudy.infrastructure.web.rest.user.request.SignupRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/signup")
    fun signUp(
        @RequestBody @Valid request: SignupRequest
    ): ResponseEntity<ApiResponse<Unit>> {
        authService.signup(request)

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(HttpStatus.CREATED, "회원가입에 성공했습니다."))
    }

}