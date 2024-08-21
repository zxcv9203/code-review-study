package org.example.codereviewstudy.infrastructure.auth.provider

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class JwtTokenProviderTest(
    key: String = "testkeytestkeytestkeytestkeytestkeytestkeytestkeytestkey",
    private val issuer: String = "test",
    private val jwtTokenProvider: JwtTokenProvider = JwtTokenProvider(
        key = key,
        expiration = 1000,
        issuer = issuer
    )
) : DescribeSpec({

    describe("JWT 토큰 생성") {
        context("유저 ID가 주어졌을 때") {
            val userId = 1L

            it("JWT 토큰을 생성한다") {
                val token = jwtTokenProvider.create(userId)

                val claims = jwtTokenProvider.getClamis(token)

                claims.issuer shouldBe issuer
                claims.subject shouldBe userId.toString()
            }
        }
    }
})
