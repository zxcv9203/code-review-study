package org.example.codereviewstudy.application.user

import org.example.codereviewstudy.domain.user.exception.UsernameDuplicatedException
import org.example.codereviewstudy.domain.user.model.User
import org.example.codereviewstudy.domain.user.port.UserRegistrationPort
import org.example.codereviewstudy.domain.user.port.UserValidationPort
import org.example.codereviewstudy.infrastructure.web.rest.user.request.SignupRequest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AuthService(
    private val passwordEncoder: PasswordEncoder,
    private val userValidationPort : UserValidationPort,
    private val userRegistrationPort: UserRegistrationPort,
) {

    fun signup(
        request: SignupRequest
    ) {
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
}