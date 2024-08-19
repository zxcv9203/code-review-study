package org.example.codereviewstudy.application.user

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.example.codereviewstudy.domain.user.exception.UsernameDuplicatedException
import org.example.codereviewstudy.domain.user.port.UserRegistrationPort
import org.example.codereviewstudy.domain.user.port.UserValidationPort
import org.example.codereviewstudy.infrastructure.web.rest.user.request.SignupRequest
import org.springframework.security.crypto.password.PasswordEncoder

class AuthServiceTest : DescribeSpec({
    val passwordEncoder = mockk<PasswordEncoder>()
    val userValidationPort = mockk<UserValidationPort>()
    val userRegistrationPort = mockk<UserRegistrationPort>()

    val authService = AuthService(
        passwordEncoder = passwordEncoder,
        userValidationPort = userValidationPort,
        userRegistrationPort = userRegistrationPort
    )

    describe("회원가입") {
        context("중복된 사용자 이름이 주어졌을 때") {
            it("UsernameDuplicatedException이 발생한다") {
                val username = "test"
                val password = "test"
                val request = SignupRequest(username, password)

                every { userValidationPort.isDuplicatedUsername(username) } returns true

                val exception = shouldThrow<UsernameDuplicatedException> {
                    authService.signup(request)
                }

                exception.username shouldBe username
            }
        }
    }

})
