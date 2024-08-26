package org.example.codereviewstudy.infrastructure.web.rest.user

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.DescribeSpec
import org.example.codereviewstudy.infrastructure.persistence.user.JpaUserRepository
import org.example.codereviewstudy.infrastructure.persistence.user.UserJpaEntity
import org.example.codereviewstudy.infrastructure.web.rest.user.request.LoginRequest
import org.example.codereviewstudy.infrastructure.web.rest.user.request.SignupRequest
import org.example.codereviewstudy.utils.restDocMockMvcBuild
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.hasSize
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.restdocs.ManualRestDocumentation
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext

@SpringBootTest
@Transactional
class AuthControllerTest(
    @Autowired
    private val context: WebApplicationContext,
    @Autowired
    private val objectMapper: ObjectMapper,
    @Autowired
    private val userRepository: JpaUserRepository,
    @Autowired
    private val passwordEncoder: PasswordEncoder,
) : DescribeSpec({

    val restDocumentation = ManualRestDocumentation()
    val mockMvc = restDocMockMvcBuild(context, restDocumentation)

    beforeEach {
        userRepository.save(UserJpaEntity("exists1234", passwordEncoder.encode("existPassword")))
        restDocumentation.beforeTest(javaClass, it.name.testName)
    }

    afterEach {
        userRepository.deleteAll()
        restDocumentation.afterTest()
    }

    describe("POST /api/signup") {
        context("유효한 요청이 주어졌을 때") {
            val username = "test123"
            val password = "test123124"
            val request = SignupRequest(username, password)

            it("회원가입에 성공한다") {
                val httpRequest = mockMvc.perform(
                    post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )

                httpRequest
                    .andExpect(status().isCreated)
                    .andExpect(jsonPath("$.status").value(201))
                    .andExpect(jsonPath("$.message").value("회원가입에 성공했습니다."))
                    .andDo(
                        document(
                            "signup/success",
                            requestFields(
                                fieldWithPath("username").description("사용자 이름"),
                                fieldWithPath("password").description("비밀번호"),
                            ),
                            responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("message").description("메시지"),
                                fieldWithPath("data").description("데이터")
                            )
                        )
                    )
            }
        }
        context("유효하지 않은 요청이 주어졌을 때") {
            val username = "1"
            val password = "test"
            val request = SignupRequest(username, password)
            it("회원가입에 실패한다") {
                val httpRequest = mockMvc.perform(
                    post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )

                httpRequest
                    .andExpect(status().isBadRequest)
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                    .andExpect(jsonPath("$.data", hasSize<Int>(2)))
                    .andExpect(jsonPath("$.data[*].field").value(containsInAnyOrder("username", "password")))
                    .andExpect(
                        jsonPath("$.data[*].message").value(
                            containsInAnyOrder(
                                "아이디는 4자 이상 10자 이하의 알파벳 소문자와 숫자만 허용됩니다.",
                                "비밀번호는 8자 이상 15자 이하의 알파벳 대소문자와 숫자만 허용됩니다."
                            )
                        )
                    )
                    .andDo(
                        document(
                            "signup/fail/invalid-request",
                            requestFields(
                                fieldWithPath("username").description("사용자 이름"),
                                fieldWithPath("password").description("비밀번호"),
                            ),
                            responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("message").description("메시지"),
                                fieldWithPath("data").description("데이터"),
                                fieldWithPath("data[].field").description("유효성 검사에 실패한 필드"),
                                fieldWithPath("data[].message").description("유효성 검사 실패 메시지"),
                            )
                        )
                    )
            }
        }

        context("중복된 사용자 이름이 주어졌을 때") {
            val username = "exists1234"
            val password = "test123124"
            val request = SignupRequest(username, password)
            it("회원가입에 실패한다") {
                val httpRequest = mockMvc.perform(
                    post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )

                httpRequest
                    .andExpect(status().isBadRequest)
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.message").value("이미 존재하는 사용자 이름입니다."))
                    .andDo(
                        document(
                            "signup/fail/duplicated-username",
                            requestFields(
                                fieldWithPath("username").description("사용자 이름"),
                                fieldWithPath("password").description("비밀번호"),
                            ),
                            responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("message").description("메시지"),
                                fieldWithPath("data").description("데이터")
                            )
                        )
                    )
            }
        }

        describe("POST /api/login") {
            context("유효한 요청이 주어졌을 때") {
                val username = "exists1234"
                val password = "existPassword"
                val request = SignupRequest(username, password)
                it("로그인에 성공한다") {
                    val httpRequest = mockMvc.perform(
                        post("/api/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    )

                    httpRequest
                        .andExpect(status().isOk)
                        .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                        .andExpect(jsonPath("$.message").value("로그인에 성공했습니다."))
                        .andDo(
                            document(
                                "login/success",
                                requestFields(
                                    fieldWithPath("username").description("사용자 이름"),
                                    fieldWithPath("password").description("비밀번호"),
                                ),
                                responseHeaders(
                                    headerWithName("Authorization").description("로그인 JWT 토큰")
                                ),
                                responseFields(
                                    fieldWithPath("status").description("상태 코드"),
                                    fieldWithPath("message").description("메시지"),
                                    fieldWithPath("data").description("데이터")
                                )
                            )
                        )
                }
            }
            context("존재하지 않는 사용자 이름이 주어졌을 때") {
                val username = "invalid123"
                val password = "invalid123"
                val request = LoginRequest(username, password)
                it("로그인에 실패한다") {
                    val httpRequest = mockMvc.perform(
                        post("/api/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    )

                    httpRequest
                        .andExpect(status().isBadRequest)
                        .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                        .andExpect(jsonPath("$.message").value("사용자 이름 혹은 비밀번호를 다시 확인해주세요."))
                        .andDo(
                            document(
                                "login/fail/not-exists-username",
                                requestFields(
                                    fieldWithPath("username").description("사용자 이름"),
                                    fieldWithPath("password").description("비밀번호"),
                                ),
                                responseFields(
                                    fieldWithPath("status").description("상태 코드"),
                                    fieldWithPath("message").description("메시지"),
                                    fieldWithPath("data").description("데이터")
                                )
                            )
                        )

                }
            }
            context("존재하는 사용자 이름이 주어졌으나 비밀번호가 일치하지 않을 때") {
                val username = "exists1234"
                val password = "invalid123"
                val request = LoginRequest(username, password)
                it("로그인에 실패한다") {
                    val httpRequest = mockMvc.perform(
                        post("/api/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    )

                    httpRequest
                        .andExpect(status().isBadRequest)
                        .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                        .andExpect(jsonPath("$.message").value("사용자 이름 혹은 비밀번호를 다시 확인해주세요."))
                        .andDo(
                            document(
                                "login/fail/not-match-password",
                                requestFields(
                                    fieldWithPath("username").description("사용자 이름"),
                                    fieldWithPath("password").description("비밀번호"),
                                ),
                                responseFields(
                                    fieldWithPath("status").description("상태 코드"),
                                    fieldWithPath("message").description("메시지"),
                                    fieldWithPath("data").description("데이터")
                                )
                            )
                        )
                }
            }
            context("유효하지 않은 요청이 주어졌을 때") {
                val username = "1"
                val password = "test"
                val request = SignupRequest(username, password)
                it("로그인에 실패한다") {
                    val httpRequest = mockMvc.perform(
                        post("/api/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    )

                    httpRequest
                        .andExpect(status().isBadRequest)
                        .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                        .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                        .andExpect(jsonPath("$.data", hasSize<Int>(2)))
                        .andExpect(jsonPath("$.data[*].field").value(containsInAnyOrder("username", "password")))
                        .andExpect(
                            jsonPath("$.data[*].message").value(
                                containsInAnyOrder(
                                    "아이디는 4자 이상 10자 이하의 알파벳 소문자와 숫자만 허용됩니다.",
                                    "비밀번호는 8자 이상 15자 이하의 알파벳 대소문자와 숫자만 허용됩니다."
                                )
                            )
                        )
                        .andDo(
                            document(
                                "login/fail/invalid-request",
                                requestFields(
                                    fieldWithPath("username").description("사용자 이름"),
                                    fieldWithPath("password").description("비밀번호"),
                                ),
                                responseFields(
                                    fieldWithPath("status").description("상태 코드"),
                                    fieldWithPath("message").description("메시지"),
                                    fieldWithPath("data").description("데이터"),
                                    fieldWithPath("data[].field").description("유효성 검사에 실패한 필드"),
                                    fieldWithPath("data[].message").description("유효성 검사 실패 메시지"),
                                )
                            )
                        )
                }
            }
        }
    }
})
