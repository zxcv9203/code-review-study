package org.example.codereviewstudy.infrastructure.web.rest.post

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.forAll
import io.kotest.data.row
import org.example.codereviewstudy.infrastructure.auth.provider.JwtTokenProvider
import org.example.codereviewstudy.infrastructure.persistence.post.JpaPostRepository
import org.example.codereviewstudy.infrastructure.persistence.user.JpaUserRepository
import org.example.codereviewstudy.infrastructure.persistence.user.UserJpaEntity
import org.example.codereviewstudy.infrastructure.web.rest.post.request.PostCreateRequest
import org.example.codereviewstudy.utils.restDocMockMvcBuild
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.restdocs.ManualRestDocumentation
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext

@SpringBootTest
@Transactional
class PostCreateControllerTest(
    @Autowired
    private val context: WebApplicationContext,
    @Autowired
    private val objectMapper: ObjectMapper,
    @Autowired
    private val userRepository: JpaUserRepository,
    @Autowired
    private val jwtTokenProvider: JwtTokenProvider,
    @Autowired
    private val postRepository: JpaPostRepository,
) : DescribeSpec({

    val restDocumentation = ManualRestDocumentation()
    val mockMvc = restDocMockMvcBuild(context, restDocumentation)
    lateinit var user: UserJpaEntity

    beforeSpec {
        user = userRepository.saveAndFlush(UserJpaEntity("exists1234", "1111"))
    }

    beforeEach {
        restDocumentation.beforeTest(javaClass, it.name.testName)
    }

    afterEach {
        postRepository.deleteAll()
        restDocumentation.afterTest()
    }

    afterSpec {
        userRepository.deleteAll()
    }

    describe("게시글 생성") {
        context("올바른 데이터가 주어지면") {
            val request = PostCreateRequest("title", "content")
            val token = jwtTokenProvider.create(user.id)
            it("게시글을 생성한다") {

                mockMvc.perform(
                    post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer $token")
                        .content(objectMapper.writeValueAsString(request))
                )
                    .andExpect(status().isCreated)
                    .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.value()))
                    .andExpect(jsonPath("$.message").value("게시글 작성에 성공했습니다."))
                    .andExpect(jsonPath("$.data.author").value(user.username))
                    .andExpect(jsonPath("$.data.title").value(request.title))
                    .andExpect(jsonPath("$.data.content").value(request.content))
                    .andDo(
                        document(
                            "post-create/success",
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
                row("", "", "blank"),
                row("a".repeat(256), "유효한 내용", "long-title")
            ) { title, content, description ->
                val request = PostCreateRequest(title, content)
                it("400 Bad Request를 반환한다 - $description") {
                    mockMvc.perform(
                        post("/api/posts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer $token")
                            .content(objectMapper.writeValueAsString(request))
                    )
                        .andExpect(status().isBadRequest)
                        .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                        .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                        .andDo(
                            document(
                                "post-create/fail/$description",
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
                                    fieldWithPath("data[].field").description("유효성 검사에 실패한 필드"),
                                    fieldWithPath("data[].message").description("유효성 검사 실패 메시지"),
                                )
                            )
                        )
                }
            }
        }
        context("존재하지 않는 사용자인 경우") {
            it("404 Not Found를 반환한다") {
                val request = PostCreateRequest("title", "content")
                val token = jwtTokenProvider.create(0)

                mockMvc.perform(
                    post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer $token")
                        .content(objectMapper.writeValueAsString(request))
                )
                    .andExpect(status().isNotFound)
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.message").value("사용자를 찾을 수 없습니다."))
                    .andDo(
                        document(
                            "post-create/fail/not-found-user",
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
                                fieldWithPath("data").description("데이터")
                            )
                        )
                    )
            }
        }
    }
})