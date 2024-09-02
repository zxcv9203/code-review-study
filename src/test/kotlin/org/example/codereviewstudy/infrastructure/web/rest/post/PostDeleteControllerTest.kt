package org.example.codereviewstudy.infrastructure.web.rest.post

import io.kotest.core.spec.style.DescribeSpec
import org.example.codereviewstudy.domain.post.exception.model.PostErrorMessage
import org.example.codereviewstudy.domain.user.exception.model.UserErrorMessage
import org.example.codereviewstudy.infrastructure.auth.provider.JwtTokenProvider
import org.example.codereviewstudy.infrastructure.persistence.post.SpringDataJpaPostRepository
import org.example.codereviewstudy.infrastructure.persistence.post.PostJpaEntity
import org.example.codereviewstudy.infrastructure.persistence.user.JpaUserRepository
import org.example.codereviewstudy.infrastructure.persistence.user.UserJpaEntity
import org.example.codereviewstudy.infrastructure.web.rest.post.response.PostSuccessMessage
import org.example.codereviewstudy.utils.restDocMockMvcBuild
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.restdocs.ManualRestDocumentation
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.context.WebApplicationContext

@SpringBootTest
class PostDeleteControllerTest(
    @Autowired
    private val context: WebApplicationContext,
    @Autowired
    private val userRepository: JpaUserRepository,
    @Autowired
    private val jwtTokenProvider: JwtTokenProvider,
    @Autowired
    private val postRepository: SpringDataJpaPostRepository,
) : DescribeSpec({
    val restDocumentation = ManualRestDocumentation()
    val mockMvc = restDocMockMvcBuild(context, restDocumentation)

    lateinit var user: UserJpaEntity
    lateinit var otherUser: UserJpaEntity
    lateinit var post: PostJpaEntity

    beforeSpec {
        user = userRepository.saveAndFlush(UserJpaEntity("exists1234", "1111"))
        otherUser = userRepository.saveAndFlush(UserJpaEntity("otherUser12", "1111"))
    }

    beforeEach {
        post = postRepository.saveAndFlush(
            PostJpaEntity(
                title = "title",
                content = "content",
                author = user
            )
        )
        restDocumentation.beforeTest(javaClass, it.name.testName)
    }

    afterEach {
        postRepository.deleteAll()
        restDocumentation.afterTest()
    }

    afterSpec {
        userRepository.deleteAll()
    }

    describe("게시글 삭제") {
        context("존재하는 게시글 ID가 주어지면") {
            val token = jwtTokenProvider.create(user.id)
            it("해당 게시글을 삭제한다.") {
                mockMvc.perform(
                    delete("/api/posts/{id}", post.id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                    .andExpect(jsonPath("$.message").value(PostSuccessMessage.DELETED.message))
                    .andDo(
                        document(
                            "post-delete/success",
                            requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT 토큰")
                            ),
                            pathParameters(
                                parameterWithName("id").description("게시글 ID")
                            ),
                            responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data").description("데이터")
                            )
                        )
                    )
            }
        }
        context("다른 사용자가 작성한 게시글을 삭제하려고 하면") {
            val token = jwtTokenProvider.create(otherUser.id)
            it("403 Forbidden을 반환한다.") {
                mockMvc.perform(
                    delete("/api/posts/{id}", post.id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                )
                    .andExpect(status().isForbidden)
                    .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()))
                    .andExpect(jsonPath("$.message").value(PostErrorMessage.AUTHOR_NOT_MATCHED.message))
                    .andDo(
                        document(
                            "post-delete/fail/not-author",
                            requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT 토큰")
                            ),
                            pathParameters(
                                parameterWithName("id").description("게시글 ID")
                            ),
                            responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data").description("데이터")
                            )
                        )
                    )
            }
        }
        context("존재하지 않는 게시글 ID가 주어지면") {
            val token = jwtTokenProvider.create(user.id)
            val postId = 0L
            it("404를 반환한다.") {
                mockMvc.perform(
                    delete("/api/posts/{id}", postId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                )
                    .andExpect(status().isNotFound)
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.message").value(PostErrorMessage.NOT_FOUND.message))
                    .andDo(
                        document(
                            "post-delete/fail/not-found-post",
                            requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT 토큰")
                            ),
                            pathParameters(
                                parameterWithName("id").description("게시글 ID")
                            ),
                            responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data").description("데이터")
                            )
                        )
                    )
            }
        }
        context("존재하지 않는 사용자인 경우") {
            it("404를 반환한다.") {
                val token = jwtTokenProvider.create(0)

                mockMvc.perform(
                    delete("/api/posts/{id}", post.id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                )
                    .andExpect(status().isNotFound)
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.message").value(UserErrorMessage.NOT_FOUND.message))
                    .andDo(
                        document(
                            "post-delete/fail/not-found-user",
                            requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT 토큰")
                            ),
                            pathParameters(
                                parameterWithName("id").description("게시글 ID")
                            ),
                            responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data").description("데이터")
                            )
                        )
                    )
            }
        }
    }
})