package org.example.codereviewstudy.application.user

import org.example.codereviewstudy.domain.user.exception.PasswordNotMatchedException
import org.example.codereviewstudy.domain.user.exception.UsernameDuplicatedException
import org.example.codereviewstudy.domain.user.model.User
import org.example.codereviewstudy.domain.user.port.UserQueryPort
import org.example.codereviewstudy.domain.user.port.UserRegistrationPort
import org.example.codereviewstudy.domain.user.port.UserValidationPort
import org.example.codereviewstudy.infrastructure.auth.provider.JwtTokenProvider
import org.example.codereviewstudy.infrastructure.web.rest.user.request.LoginRequest
import org.example.codereviewstudy.infrastructure.web.rest.user.request.SignupRequest
import org.example.codereviewstudy.infrastructure.web.rest.user.response.LoginResponse
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AuthService(
    private val jwtTokenProvider: JwtTokenProvider,

    private val passwordEncoder: PasswordEncoder,

    private val userValidationPort: UserValidationPort,
    private val userRegistrationPort: UserRegistrationPort,
    private val userQueryPort: UserQueryPort,
) {

    fun signup(request: SignupRequest) {
        if (userValidationPort.isDuplicatedUsername(request.username)) {
            throw UsernameDuplicatedException(request.username)
        }

        val encodedPassword = passwordEncoder.encode(request.password)
        val user = User(
            username = request.username,
            password = encodedPassword
        )
        userRegistrationPort.signup(user)
    }

    fun login(request: LoginRequest): LoginResponse {
        val user = userQueryPort.findByUsername(request.username)
        if (!passwordEncoder.matches(request.password, user.password)) {
            throw PasswordNotMatchedException(request.password, user.password)
        }

        val token = jwtTokenProvider.create(user.id)
        return LoginResponse(token)
    }
}