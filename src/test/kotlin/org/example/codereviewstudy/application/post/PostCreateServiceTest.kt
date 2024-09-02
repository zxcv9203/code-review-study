package org.example.codereviewstudy.application.post

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.example.codereviewstudy.application.user.UserQueryService
import org.example.codereviewstudy.domain.post.port.PostCreatePort
import org.example.codereviewstudy.domain.user.model.User
import org.example.codereviewstudy.infrastructure.auth.model.AuthUser
import org.example.codereviewstudy.infrastructure.web.rest.post.mapper.toDomain
import org.example.codereviewstudy.infrastructure.web.rest.post.request.PostCreateRequest

class PostCreateServiceTest(
    private val userQueryService: UserQueryService = mockk<UserQueryService>(),
    private val postCreatePort: PostCreatePort = mockk<PostCreatePort>(),
    private val postCreateService: PostCreateService = PostCreateService(userQueryService, postCreatePort)
) : DescribeSpec({

    describe("게시글을 생성할 때") {
        val authUser = AuthUser(1)
        val user = User(id = 1, username = "test", password = "password")
        val request = PostCreateRequest(title = "title", content = "content")

        context("올바른 데이터가 주어지면") {
            it("게시글을 생성한다") {
                every { userQueryService.findById(authUser.id) } returns user
                every { postCreatePort.create(any()) } returns request.toDomain(user)

                val got = postCreateService.create(request, authUser)

                got.title shouldBe request.title
                got.content shouldBe request.content
            }
        }
    }
})
