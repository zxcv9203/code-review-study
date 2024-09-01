package org.example.codereviewstudy.application.post

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.example.codereviewstudy.application.user.UserQueryService
import org.example.codereviewstudy.domain.post.exception.PostAuthorNotMatchedException
import org.example.codereviewstudy.domain.post.exception.model.PostErrorMessage
import org.example.codereviewstudy.domain.post.model.Post
import org.example.codereviewstudy.domain.post.port.PostQueryPort
import org.example.codereviewstudy.domain.post.port.PostUpdatePort
import org.example.codereviewstudy.domain.user.model.User
import org.example.codereviewstudy.infrastructure.web.rest.post.request.PostUpdateRequest
import java.time.LocalDateTime

class PostUpdateServiceTest(
    private val userQueryService: UserQueryService = mockk<UserQueryService>(),
    private val postQueryPort: PostQueryPort = mockk<PostQueryPort>(),
    private val postUpdatePort: PostUpdatePort = mockk<PostUpdatePort>(),
    private val postUpdateService: PostUpdateService = PostUpdateService(userQueryService, postQueryPort, postUpdatePort)
) : DescribeSpec({

    describe("게시글을 수정할 때") {
        context("올바른 데이터가 주어지면") {
            val id = 1L
            val request = PostUpdateRequest(title = "title", content = "content")
            val userId = 1L
            val dbPost = Post(id = id, title = "old title", content = "old content", author = User(id = 1, username = "test", password = "password"), createdAt = LocalDateTime.now())

            every { userQueryService.findById(userId) } returns User(id = userId, username = "test", password = "password")
            every { postQueryPort.findById(id) } returns dbPost
            every { postUpdatePort.update(dbPost) } returns dbPost
            it("게시글을 수정한다") {
                val got = postUpdateService.update(id, request, userId)

                got.title shouldBe request.title
                got.content shouldBe request.content
            }
        }

        context("작성한 사용자와 로그인 사용자가 다르다면") {
            val id = 1L
            val request = PostUpdateRequest(title = "title", content = "content")
            val userId = 1L
            val dbPost = Post(id = id, title = "old title", content = "old content", author = User(id = 2, username = "test", password = "password"), createdAt = LocalDateTime.now())

            every { userQueryService.findById(userId) } returns User(id = userId, username = "test", password = "password")
            every { postQueryPort.findById(id) } returns dbPost
            it("게시글을 수정할 수 없다") {
                val exception = shouldThrow<PostAuthorNotMatchedException> {
                    postUpdateService.update(id, request, userId)
                }
                exception.authorId shouldBe dbPost.author.id
                exception.loginUserId shouldBe userId
                exception.message shouldBe PostErrorMessage.AUTHOR_NOT_MATCHED.message
                exception.errorMessage shouldBe "${PostErrorMessage.AUTHOR_NOT_MATCHED.message} : 작성자 ID: ${dbPost.author.id}, 로그인 사용자 ID: $userId"
            }
        }
    }
})
