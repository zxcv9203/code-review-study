package org.example.codereviewstudy.infrastructure.web.rest.user.request

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import jakarta.validation.ConstraintViolation
import jakarta.validation.Validation
import jakarta.validation.Validator
import jakarta.validation.ValidatorFactory

class LoginRequestTest : DescribeSpec({
    lateinit var validator: Validator

    beforeSpec {
        val factory: ValidatorFactory = Validation.buildDefaultValidatorFactory()
        validator = factory.validator
    }

    describe("SignUpRequest 유효성 검사") {

        context("올바른 데이터가 제공되었을 때") {
            it("유효성 검사를 통과해야 한다") {
                val request = LoginRequest(username = "testuser", password = "Password123")
                val violations: Set<ConstraintViolation<LoginRequest>> = validator.validate(request)
                violations.size shouldBe 0
            }
        }

        context("잘못된 아이디가 제공되었을 때") {
            it("아이디에 대한 유효성 검사가 실패해야 한다") {
                val request = LoginRequest(username = "invalidUsername123", password = "Password123")
                val violations: Set<ConstraintViolation<LoginRequest>> = validator.validate(request)
                violations.size shouldBe 1
                violations.first().message shouldBe "아이디는 4자 이상 10자 이하의 알파벳 소문자와 숫자만 허용됩니다."
            }
        }

        context("잘못된 비밀번호가 제공되었을 때") {
            it("비밀번호에 대한 유효성 검사가 실패해야 한다") {
                val request = LoginRequest(username = "testuser", password = "short")
                val violations: Set<ConstraintViolation<LoginRequest>> = validator.validate(request)
                violations.size shouldBe 1
                violations.first().message shouldBe "비밀번호는 8자 이상 15자 이하의 알파벳 대소문자와 숫자만 허용됩니다."
            }
        }

        context("아이디와 비밀번호 모두 잘못된 경우") {
            it("두 필드 모두 유효성 검사가 실패해야 한다") {
                val request = LoginRequest(username = "invalidUser123", password = "short")
                val violations: Set<ConstraintViolation<LoginRequest>> = validator.validate(request)
                violations.size shouldBe 2
            }
        }
    }

})
