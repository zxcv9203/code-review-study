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
import org.example.codereviewstudy.domain.user.model.User
import org.example.codereviewstudy.domain.user.port.PostDeletePort
import java.time.LocalDateTime

class PostDeleteServiceTest(
    private val userQueryService: UserQueryService = mockk<UserQueryService>(),
    private val postQueryPort: PostQueryPort = mockk<PostQueryPort>(),
    private val postDeletePort: PostDeletePort = mockk<PostDeletePort>(),
    private val postDeleteService: PostDeleteService = PostDeleteService(
        userQueryService,
        postQueryPort,
        postDeletePort
    )
) : DescribeSpec({

    describe("게시글을 삭제할 때") {
        context("올바른 데이터가 주어지면") {
            val postId = 1L
            val userId = 1L
            val dbPost = Post(
                id = postId,
                title = "title",
                content = "content",
                author = User(id = 1, username = "test", password = "password"),
                createdAt = LocalDateTime.now()
            )
            every { userQueryService.findById(userId) } returns User(id = userId, username = "test", password = "password")
            every { postQueryPort.findById(postId) } returns dbPost
            every { postDeletePort.delete(dbPost) } returns Unit
            it("게시글을 삭제한다") {
                postDeleteService.delete(postId, userId)
            }
        }
        context("작성한 사용자와 로그인 사용자가 다르다면") {
            val postId = 1L
            val userId = 2L
            val dbPost = Post(
                id = postId,
                title = "title",
                content = "content",
                author = User(id = 1, username = "test", password = "password"),
                createdAt = LocalDateTime.now()
            )
            every { userQueryService.findById(userId) } returns User(id = userId, username = "test", password = "password")
            every { postQueryPort.findById(postId) } returns dbPost
            it("게시글을 삭제할 수 없다") {
                val exception = shouldThrow<PostAuthorNotMatchedException> {
                    postDeleteService.delete(postId, userId)
                }
                exception.authorId shouldBe dbPost.author.id
                exception.loginUserId shouldBe userId
                exception.message shouldBe PostErrorMessage.AUTHOR_NOT_MATCHED.message
                exception.errorMessage shouldBe "${PostErrorMessage.AUTHOR_NOT_MATCHED.message} : 작성자 ID: ${dbPost.author.id}, 로그인 사용자 ID: $userId"
            }
        }
    }
})
