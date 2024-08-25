package org.example.codereviewstudy.application.user

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.example.codereviewstudy.domain.user.exception.PasswordNotMatchedException
import org.example.codereviewstudy.domain.user.exception.UserNotFoundException
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

class AuthServiceTest(
    private val passwordEncoder: PasswordEncoder = mockk<PasswordEncoder>(),
    private val userValidationPort: UserValidationPort = mockk<UserValidationPort>(),
    private val userRegistrationPort: UserRegistrationPort = mockk<UserRegistrationPort>(),
    private val jwtTokenProvider: JwtTokenProvider = mockk<JwtTokenProvider>(),
    private val userQueryPort: UserQueryPort = mockk<UserQueryPort>(),
    private val authService: AuthService = AuthService(
        jwtTokenProvider = jwtTokenProvider,
        passwordEncoder = passwordEncoder,
        userValidationPort = userValidationPort,
        userRegistrationPort = userRegistrationPort,
        userQueryPort = userQueryPort
    )
) : DescribeSpec({
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

    describe("로그인") {
        context("DB에 존재하는 사용자와 비밀번호가 주어졌을 때") {
            val username = "test12345"
            val password = "test12345"
            val request = LoginRequest(username, password)

            it("로그인에 성공한다") {
                val token = "mock-token"
                val user = User(
                    username = username,
                    password = password
                )
                val want = LoginResponse(token)

                every { userQueryPort.findByUsername(username) } returns user
                every { passwordEncoder.matches(request.password, user.password) } returns true
                every { jwtTokenProvider.create(user.id) } returns token


                val got = authService.login(request)

                got shouldBe want
            }
        }
        context("DB에 존재하지 않는 사용자이름이 주어졌을 때") {
            val username = "invalid123"
            val password = "invalid123"
            val request = LoginRequest(username, password)

            it("UserNotFoundException이 발생한다") {
                every { userQueryPort.findByUsername(username) } throws UserNotFoundException(username)

                shouldThrow<UserNotFoundException> {
                    authService.login(request)
                }
            }
        }

        context("DB에 존재하는 사용자이름이 주어졌으나 비밀번호가 일치하지 않을 때") {
            val username = "test12345"
            val password = "invalid123"
            val request = LoginRequest(username, password)

            it("PasswordNotMatchedException이 발생한다") {
                val user = User(
                    username = username,
                    password = "test12345"
                )

                every { userQueryPort.findByUsername(username) } returns user
                every { passwordEncoder.matches(request.password, user.password) } returns false

                shouldThrow<PasswordNotMatchedException> {
                    authService.login(request)
                }
            }
        }
    }
})
