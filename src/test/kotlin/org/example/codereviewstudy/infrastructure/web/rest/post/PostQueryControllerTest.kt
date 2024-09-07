package org.example.codereviewstudy.infrastructure.web.rest.post

import io.kotest.core.spec.style.DescribeSpec
import org.example.codereviewstudy.common.exception.message.ErrorMessage
import org.example.codereviewstudy.domain.post.exception.model.PostErrorMessage
import org.example.codereviewstudy.infrastructure.auth.provider.JwtTokenProvider
import org.example.codereviewstudy.infrastructure.persistence.post.PostJpaEntity
import org.example.codereviewstudy.infrastructure.persistence.post.SpringDataJpaPostRepository
import org.example.codereviewstudy.infrastructure.persistence.user.JpaUserRepository
import org.example.codereviewstudy.infrastructure.persistence.user.UserJpaEntity
import org.example.codereviewstudy.utils.TxHelper
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
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
    private val postRepository: SpringDataJpaPostRepository,
    @Autowired
    private val transaction: TxHelper,
) : DescribeSpec({

    val restDocumentation = ManualRestDocumentation()
    val mockMvc = restDocMockMvcBuild(context, restDocumentation)

    lateinit var user: UserJpaEntity
    lateinit var post: PostJpaEntity
    lateinit var lastPost: PostJpaEntity

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
            val savePosts = postRepository.saveAllAndFlush(
                (1..9).map {
                    PostJpaEntity(
                        title = "title$it",
                        content = "content$it",
                        author = user
                    )
                }
            )
            lastPost = savePosts.last()
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
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.message").value(PostErrorMessage.NOT_FOUND.message))
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

    describe("게시글 리스트 조회 (커서기반)") {
        val token = jwtTokenProvider.create(user.id)
        context("게시글 아이디를 전달하지 않고 게시글보다 적은 수를 요청하면") {
            it("id 내림차순으로 정렬하며, 마지막 페이지 여부를 false로 반환한다.") {
                mockMvc.perform(
                    get("/api/posts")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                        .param("size", "5")
                )
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.data.posts.length()").value(5))
                    .andExpect(jsonPath("$.data.isLast").value(false))

            }
        }
        context("게시글 아이디를 전달하지 않고 저장된 게시글 개수 이상을 요청하면") {
            it("id 내림차순으로 정렬하며, 마지막 페이지 여부를 true로 반환한다.") {
                mockMvc.perform(
                    get("/api/posts")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                )
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.data.posts.length()").value(10))
                    .andExpect(jsonPath("$.data.isLast").value(true))
            }
        }
        context("게시글 아이디만 전달하면") {
            it("게시글을 내림차순으로 정렬한다.") {
                println(post.id.toString())
                mockMvc.perform(
                    get("/api/posts")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                        .param("id", lastPost.id.toString())
                )
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.data.posts.length()").value(9))
                    .andExpect(jsonPath("$.data.isLast").value(true))
            }
        }
        context("게시글 아이디만 전달하고 오름차순으로 정렬하면") {
            it("게시글을 오름차순으로 정렬한다") {
                println(post.id.toString())
                mockMvc.perform(
                    get("/api/posts")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                        .param("id", post.id.toString())
                        .param("sort", "asc")
                )
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.data.posts.length()").value(9))
                    .andExpect(jsonPath("$.data.isLast").value(true))
            }
        }

        context("정렬 조건을 asc 또는 desc로 하지 않는 경우") {
            it("400을 반환한다.") {
                println(post.id.toString())
                mockMvc.perform(
                    get("/api/posts")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                        .param("id", post.id.toString())
                        .param("sort", "invalid")
                )
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isBadRequest)
                    .andExpect(jsonPath("$.message").value(ErrorMessage.NOT_MATCHED_SORT_VALUE.message))
            }
        }
    }
})
