package org.example.codereviewstudy.infrastructure.web.rest.post.request

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import jakarta.validation.Validation
import jakarta.validation.Validator
import jakarta.validation.ValidatorFactory

class PostCreateRequestTest : DescribeSpec({
    lateinit var validator: Validator

    beforeSpec {
        val factory: ValidatorFactory = Validation.buildDefaultValidatorFactory()
        validator = factory.validator
    }

    describe("PostCreateRequest 유효성 검사") {

        context("올바른 데이터가 제공되었을 때") {
            it("유효성 검사를 통과해야 한다") {
                val request = PostCreateRequest(title = "title", content = "content")
                val violations = validator.validate(request)
                violations.size shouldBe 0
            }
        }

        context("제목이 비어있을 때") {
            it("제목에 대한 유효성 검사가 실패해야 한다") {
                val request = PostCreateRequest(title = "", content = "content")
                val violations = validator.validate(request)
                violations.size shouldBe 1
                violations.first().message shouldBe "게시글 제목을 입력해주세요."
            }
        }

        context("본문이 비어있을 때") {
            it("본문에 대한 유효성 검사가 실패해야 한다") {
                val request = PostCreateRequest(title = "title", content = "")
                val violations = validator.validate(request)
                violations.size shouldBe 1
                violations.first().message shouldBe "게시글 본문 내용을 입력해주세요."
            }
        }

        context("제목과 본문이 모두 비어있을 때") {
            it("두 필드 모두 유효성 검사가 실패해야 한다") {
                val request = PostCreateRequest(title = "", content = "")
                val violations = validator.validate(request)
                violations.size shouldBe 2
            }
        }

        context("제목이 256자 이상일 때") {
            it("제목에 대한 유효성 검사가 실패해야 한다") {
                val request = PostCreateRequest(title = "a".repeat(256), content = "content")
                val violations = validator.validate(request)
                violations.size shouldBe 1
                violations.first().message shouldBe "게시글 제목은 255자 이하로 입력해주세요."
            }
        }
    }
})
