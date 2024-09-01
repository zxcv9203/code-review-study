package org.example.codereviewstudy.application.user

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.example.codereviewstudy.domain.user.exception.UserNotFoundException
import org.example.codereviewstudy.domain.user.model.User
import org.example.codereviewstudy.domain.user.port.UserQueryPort

class UserQueryServiceTest(
    private val userQueryPort: UserQueryPort = mockk<UserQueryPort>(),
    private val userQueryService: UserQueryService = UserQueryService(userQueryPort),
) : DescribeSpec({

    describe("아이디로 사용자를 조회할 때") {
        context("존재하는 아이디가 주어지면") {
            val id = 1L
            it("사용자를 반환한다") {
                val want = mockk<User>()
                every { userQueryPort.findById(id) } returns want

                val got = userQueryService.findById(id)

                got shouldBe want
            }
        }
        context("존재하지 않는 아이디가 주어지면") {
            val id = 1L
            it("UserNotFoundException 예외가 발생한다.") {
                every { userQueryPort.findById(id) } returns null

                shouldThrow<UserNotFoundException> {
                    userQueryService.findById(id)
                }
            }
        }
    }
})
