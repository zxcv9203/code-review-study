package org.example.codereviewstudy.infrastructure.persistence.post

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.example.codereviewstudy.domain.post.exception.PostNotFoundException
import org.example.codereviewstudy.domain.post.exception.model.PostErrorMessage
import org.springframework.data.repository.findByIdOrNull

class PostRepositoryTest(
    private val jpaPostRepository: JpaPostRepository = mockk<JpaPostRepository>(),
    private val postRepository: PostRepository = PostRepository(jpaPostRepository)
) : DescribeSpec({

    describe("게시글 단일 조회시") {
        context("존재하지 않는 아이디가 주어진 경우") {
            val id = 0L

            it("PostNotFoundException이 발생해야 한다") {
                every { jpaPostRepository.findByIdOrNull(id) } returns null

                val exception = shouldThrow<PostNotFoundException> {
                    postRepository.findById(id)
                }

                exception.message shouldBe PostErrorMessage.NOT_FOUND.message
                exception.id shouldBe id
                exception.errorMessage shouldBe "${PostErrorMessage.NOT_FOUND.message} 게시글 ID: $id"
            }
        }
    }

})
