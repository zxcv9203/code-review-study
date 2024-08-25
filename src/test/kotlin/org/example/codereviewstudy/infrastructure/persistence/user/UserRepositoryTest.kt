package org.example.codereviewstudy.infrastructure.persistence.user

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.example.codereviewstudy.domain.user.exception.UserNotFoundException

class UserRepositoryTest(
    private val jpaUserRepository: JpaUserRepository = mockk<JpaUserRepository>(),
    private val userRepository: UserRepository = UserRepository(jpaUserRepository)
) : DescribeSpec({

    describe("사용자 이름으로 사용자 조회") {
        context("존재하지 않는 사용자가 주어졌을 때") {
            val username = "notExist"

            it("UserNotFoundException이 발생한다") {
                every { jpaUserRepository.findByUsername(username) } returns null

                shouldThrow<UserNotFoundException> {
                    userRepository.findByUsername(username)
                }
            }
        }
    }
})
