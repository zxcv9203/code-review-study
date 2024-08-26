package org.example.codereviewstudy.infrastructure.auth.resolver

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.example.codereviewstudy.infrastructure.auth.exception.InvalidTokenException
import org.example.codereviewstudy.infrastructure.auth.model.AuthUser
import org.example.codereviewstudy.infrastructure.auth.provider.JwtTokenProvider
import org.springframework.core.MethodParameter
import org.springframework.http.HttpHeaders
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.ServletWebRequest


class AuthArgumentResolverTest(
    private val jwtTokenProvider: JwtTokenProvider = mockk<JwtTokenProvider>(),
    private val authArgumentResolver: AuthArgumentResolver = AuthArgumentResolver(jwtTokenProvider)
) : DescribeSpec({

    describe("supportsParameter 메서드는") {
        context("AuthUser 타입의 파라미터가 주어졌을 때") {
            it("true를 반환해야 한다") {
                val methodParameter = mockk<MethodParameter> {
                    every { parameterType } returns AuthUser::class.java
                }
                authArgumentResolver.supportsParameter(methodParameter) shouldBe true
            }
        }
        context("AuthUser 타입이 아닌 파라미터가 주어졌을 때") {
            it("false를 반환해야 한다") {
                val methodParameter = mockk<MethodParameter> {
                    every { parameterType } returns String::class.java
                }
                authArgumentResolver.supportsParameter(methodParameter) shouldBe false
            }
        }
    }

    describe("resolveArgument 메서드는") {
        context("올바른 토큰이 주어졌을 때") {
            val want = AuthUser(1L)
            val validToken = "valid-token"
            val authorizationHeader = MockHttpServletRequest().apply {
                addHeader(HttpHeaders.AUTHORIZATION, "Bearer $validToken")
            }
            val webRequest = ServletWebRequest(authorizationHeader)
            it("AuthUser 객체를 반환해야 한다") {
                every { jwtTokenProvider.getClaims(validToken) } returns createClaims("1")

                val methodParameter = mockk<MethodParameter> {
                    every { parameterType } returns AuthUser::class.java
                }
                val authUser = authArgumentResolver.resolveArgument(
                    methodParameter, null, webRequest, null
                )

                authUser shouldBe want
            }

        }
        context("토큰이 주어지지 않았을 때") {
            val webRequest = ServletWebRequest(MockHttpServletRequest())
            it("InvalidTokenException을 던져야 한다") {
                val methodParameter = mockk<MethodParameter> {
                    every { parameterType } returns AuthUser::class.java
                }

                val exception = shouldThrow<InvalidTokenException> {
                    authArgumentResolver.resolveArgument(
                        methodParameter, null, webRequest, null
                    )
                }
                exception.message shouldBe "유효하지 않은 토큰입니다."
                exception.token shouldBe null
            }
        }
        context("Bearer 토큰이 아닌 경우") {
            val invalidToken = "invalid-token"
            val authorizationHeader = MockHttpServletRequest().apply {
                addHeader(HttpHeaders.AUTHORIZATION, invalidToken)
            }
            val webRequest = ServletWebRequest(authorizationHeader)
            it("InvalidTokenException을 던져야 한다") {
                val methodParameter = mockk<MethodParameter> {
                    every { parameterType } returns AuthUser::class.java
                }

                val exception = shouldThrow<InvalidTokenException> {
                    authArgumentResolver.resolveArgument(
                        methodParameter, null, webRequest, null
                    )
                }
                exception.message shouldBe "유효하지 않은 토큰입니다."
                exception.token shouldBe invalidToken
            }
        }
        context("유효한 JWT 토큰이 아닌 경우") {
            val invalidToken = "invalid-token"
            val authorizationHeader = MockHttpServletRequest().apply {
                addHeader(HttpHeaders.AUTHORIZATION, "Bearer $invalidToken")
            }
            val webRequest = ServletWebRequest(authorizationHeader)

            it("InvalidTokenException을 던져야 한다") {
                every { jwtTokenProvider.getClaims(invalidToken) } throws InvalidTokenException(invalidToken)

                val methodParameter = mockk<MethodParameter> {
                    every { parameterType } returns AuthUser::class.java
                }

                val exception = shouldThrow<InvalidTokenException> {
                    authArgumentResolver.resolveArgument(
                        methodParameter, null, webRequest, null
                    )
                }
                exception.message shouldBe "유효하지 않은 토큰입니다."
                exception.token shouldBe invalidToken
            }
        }
    }
}) {
    companion object {
        fun createClaims(subject: String): Claims {
            return Jwts.claims()
                .subject(subject)
                .build()
        }
    }
}