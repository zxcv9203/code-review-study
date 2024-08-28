package org.example.codereviewstudy.infrastructure.web.rest.post

import io.kotest.core.spec.style.DescribeSpec
import org.example.codereviewstudy.infrastructure.auth.provider.JwtTokenProvider
import org.example.codereviewstudy.infrastructure.persistence.post.JpaPostRepository
import org.example.codereviewstudy.infrastructure.persistence.post.PostJpaEntity
import org.example.codereviewstudy.infrastructure.persistence.user.JpaUserRepository
import org.example.codereviewstudy.infrastructure.persistence.user.UserJpaEntity
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext

@SpringBootTest
@Transactional
class PostQueryControllerTest(
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
) : DescribeSpec({

    val restDocumentation = ManualRestDocumentation()
    val mockMvc = restDocMockMvcBuild(context, restDocumentation)

    lateinit var user: UserJpaEntity
    lateinit var post: PostJpaEntity

    beforeSpec {
        transaction.exec {
            user = userRepository.saveAndFlush(UserJpaEntity("exists1234", "1111"))
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

    describe("게시글 단일 조회") {
        context("존재하는 게시글 ID가 주어지면") {
            val token = jwtTokenProvider.create(user.id)
            it("해당 게시글을 반환한다") {
                mockMvc.perform(
                    get("/api/posts/{id}", post.id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.data.id").value(post.id))
                    .andExpect(jsonPath("$.data.title").value("title"))
                    .andExpect(jsonPath("$.data.content").value("content"))
                    .andDo(
                        document(
                            "post-get-by-id/success",
                            requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT 토큰")
                            ),
                            pathParameters(
                                parameterWithName("id").description("게시글 ID")
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
        context("존재하지 않는 게시글 ID가 주어지면") {
            val token = jwtTokenProvider.create(user.id)
            val postId = 0L
            it("404를 반환한다") {
                mockMvc.perform(
                    get("/api/posts/{id}", postId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                    .andExpect(status().isNotFound)
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").value("해당 게시글을 찾을 수 없습니다."))
                    .andDo(
                        document(
                            "post-get-by-id/not-found",
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
