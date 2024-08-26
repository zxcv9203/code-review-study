package org.example.codereviewstudy.infrastructure.auth.provider

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.example.codereviewstudy.infrastructure.auth.exception.InvalidTokenException

class JwtTokenProviderTest(
    key: String = "testkeytestkeytestkeytestkeytestkeytestkeytestkeytestkey",
    private val issuer: String = "test",
    private val jwtTokenProvider: JwtTokenProvider = JwtTokenProvider(
        key = key,
        expiration = 100000,
        issuer = issuer
    )
) : DescribeSpec({

    describe("JWT 토큰 생성") {
        context("유저 ID가 주어졌을 때") {
            val userId = 1L

            it("JWT 토큰을 생성한다") {
                val token = jwtTokenProvider.create(userId)

                val claims = jwtTokenProvider.getClaims(token)

                claims.issuer shouldBe issuer
                claims.subject shouldBe userId.toString()
            }
        }
    }

    describe("JWT 토큰 조회") {
        context("만료된 토큰이 주어졌을 때") {
            val expiredJwtTokenProvider = JwtTokenProvider(key, issuer, -1000L)
            val token = expiredJwtTokenProvider.create(1L)
            it("유효성 검사를 통과하지 못한다") {
                shouldThrow<InvalidTokenException> {
                    jwtTokenProvider.getClaims(token)
                }
            }
        }
    }
})
