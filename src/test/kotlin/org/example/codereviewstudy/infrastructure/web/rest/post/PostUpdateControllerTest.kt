package org.example.codereviewstudy.infrastructure.web.rest.post

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.forAll
import io.kotest.data.row
import org.example.codereviewstudy.infrastructure.auth.provider.JwtTokenProvider
import org.example.codereviewstudy.infrastructure.persistence.post.JpaPostRepository
import org.example.codereviewstudy.infrastructure.persistence.post.PostJpaEntity
import org.example.codereviewstudy.infrastructure.persistence.user.JpaUserRepository
import org.example.codereviewstudy.infrastructure.persistence.user.UserJpaEntity
import org.example.codereviewstudy.infrastructure.web.rest.post.request.PostCreateRequest
import org.example.codereviewstudy.infrastructure.web.rest.post.request.PostUpdateRequest
import org.example.codereviewstudy.utils.TxHelper
import org.example.codereviewstudy.utils.restDocMockMvcBuild
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.restdocs.ManualRestDocumentation
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.context.WebApplicationContext

@SpringBootTest
class PostUpdateControllerTest(
    @Autowired
    private val context: WebApplicationContext,
    @Autowired
    private val userRepository: JpaUserRepository,
    @Autowired
    private val jwtTokenProvider: JwtTokenProvider,
    @Autowired
    private val postRepository: JpaPostRepository,
    @Autowired
    private val transaction: TxHelper,
    @Autowired
    private val objectMapper: ObjectMapper,
) : DescribeSpec({
    val restDocumentation = ManualRestDocumentation()
    val mockMvc = restDocMockMvcBuild(context, restDocumentation)

    lateinit var user: UserJpaEntity
    lateinit var otherUser: UserJpaEntity
    lateinit var post: PostJpaEntity

    beforeSpec {
        transaction.exec {
            user = userRepository.saveAndFlush(UserJpaEntity("exists1234", "1111"))
            otherUser = userRepository.saveAndFlush(UserJpaEntity("otherUser12", "1111"))
            post = postRepository.saveAndFlush(
                PostJpaEntity(
                    title = "title",
                    content = "content",
                    author = user
                )
            )
        }
    }

    beforeEach {
        restDocumentation.beforeTest(javaClass, it.name.testName)
    }

    afterEach {
        restDocumentation.afterTest()
    }

    afterSpec {
        postRepository.deleteAll()
        userRepository.deleteAll()
    }

    describe("게시글 수정") {
        context("존재하는 게시글 ID가 주어지면") {
            val token = jwtTokenProvider.create(user.id)
            val request = PostUpdateRequest("new title", "new content")
            it("해당 게시글을 수정한다") {
                mockMvc.perform(
                    patch("/api/posts/{id}", post.id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.message").value("게시글 수정에 성공했습니다."))
                    .andExpect(jsonPath("$.data.title").value(request.title))
                    .andExpect(jsonPath("$.data.content").value(request.content))
                    .andDo(
                        document(
                            "post-update/success",
                            requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT 토큰")
                            ),
                            requestFields(
                                fieldWithPath("title").description("게시글 제목"),
                                fieldWithPath("content").description("게시글 내용")
                            ),
                            responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data.id").description("게시글 ID"),
                                fieldWithPath("data.title").description("게시글 제목"),
                                fieldWithPath("data.content").description("게시글 내용"),
                                fieldWithPath("data.author").description("작성자 이름"),
                                fieldWithPath("data.createdAt").description("작성일시")
                            )
                        )
                    )
            }

        }
        context("유효하지 않은 데이터를 전달하는 경우") {
            val token = jwtTokenProvider.create(user.id)
            forAll(
                row(PostCreateRequest("", ""), "blank"),
                row(PostCreateRequest("a".repeat(256), "유효한 내용"), "long-title")
            ) { request, description ->
                it("400 Bad Request를 반환한다 - $description") {
                    mockMvc.perform(
                        patch("/api/posts/{id}", post.id)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    )
                        .andExpect(status().isBadRequest)
                        .andExpect(jsonPath("$.status").value(400))
                        .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                        .andDo(
                            document(
                                "post-update/fail/$description",
                                requestFields(
                                    fieldWithPath("title").description("게시글 제목"),
                                    fieldWithPath("content").description("게시글 내용")
                                ),
                                responseFields(
                                    fieldWithPath("status").description("상태 코드"),
                                    fieldWithPath("message").description("응답 메시지"),
                                    fieldWithPath("data").description("데이터"),
                                    fieldWithPath("data[].field").description("유효성 검사에 실패한 필드"),
                                    fieldWithPath("data[].message").description("유효성 검사 실패 메시지"),
                                )
                            )
                        )
                }
            }
        }
        context("존재하지 않는 게시글 ID가 주어지면") {
            val token = jwtTokenProvider.create(user.id)
            val postId = 0L
            val request = PostUpdateRequest("new title", "new content")
            it("404 NotFound를 반환한다") {
                mockMvc.perform(
                    patch("/api/posts/{id}", postId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                    .andExpect(status().isNotFound)
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").value("해당 게시글을 찾을 수 없습니다."))
                    .andDo(
                        document(
                            "post-update/fail/not-found",
                            requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT 토큰")
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
        context("게시글을 작성한 사용자가 수정하지 않는 경우") {
            it("403 Forbidden을 반환한다") {
                val token = jwtTokenProvider.create(otherUser.id)
                val request = PostUpdateRequest("new title", "new content")

                mockMvc.perform(
                    patch("/api/posts/{id}", post.id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                    .andExpect(status().isForbidden)
                    .andExpect(jsonPath("$.status").value(403))
                    .andExpect(jsonPath("$.message").value("내가 작성한 게시글만 수정할 수 있습니다."))
                    .andDo(
                        document(
                            "post-update/fail/not-author",
                            requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT 토큰")
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
            it("404 Not Found를 반환한다") {
                val token = jwtTokenProvider.create(0)
                val request = PostUpdateRequest("new title", "new content")

                mockMvc.perform(
                    patch("/api/posts/{id}", post.id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                    .andExpect(status().isNotFound)
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").value("사용자를 찾을 수 없습니다."))
                    .andDo(
                        document(
                            "post-update/fail/not-found-user",
                            requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT 토큰")
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
