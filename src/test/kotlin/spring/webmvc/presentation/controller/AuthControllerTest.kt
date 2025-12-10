package spring.webmvc.presentation.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import spring.webmvc.application.dto.result.TokenResult
import spring.webmvc.application.service.AuthService
import spring.webmvc.domain.model.entity.User
import spring.webmvc.domain.model.enums.UserType
import spring.webmvc.domain.model.vo.Email
import spring.webmvc.domain.model.vo.Phone
import spring.webmvc.infrastructure.config.WebMvcTestConfig
import spring.webmvc.presentation.controller.support.MockMvcRestDocsSetup
import java.time.LocalDate

@WebMvcTest(AuthController::class)
@Import(WebMvcTestConfig::class)
class AuthControllerTest : MockMvcRestDocsSetup() {
    @MockkBean
    private lateinit var authService: AuthService
    private lateinit var accessToken: String
    private lateinit var refreshToken: String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var tokenResult: TokenResult

    @BeforeEach
    fun setUp() {
        accessToken = "accessToken"
        refreshToken = "refreshToken"
        email = "test@gmail.com"
        password = "password"
        tokenResult = TokenResult(accessToken = accessToken, refreshToken = refreshToken)
    }

    @Test
    fun signUp() {
        val user = io.mockk.spyk(
            User.create(
                email = Email.create(email),
                password = password,
                name = "홍길동",
                phone = Phone.create("010-1234-5678"),
                birthDate = LocalDate.of(1990, 1, 1),
                type = UserType.CUSTOMER,
            )
        )
        every { user.id } returns 1L
        every { authService.signUp(any()) } returns user

        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                          "email": "$email",
                          "password": "$password",
                          "name": "홍길동",
                          "phone": "010-1234-5678",
                          "birthDate": "1990-01-01",
                          "type": "CUSTOMER",
                          "roleIds": [],
                          "permissionIds": []
                        }
                    """.trimIndent()
                )
        )
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andDo(
                MockMvcRestDocumentation.document(
                    "sign-up",
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("email").description("이메일"),
                        PayloadDocumentation.fieldWithPath("password").description("패스워드"),
                        PayloadDocumentation.fieldWithPath("name").description("이름"),
                        PayloadDocumentation.fieldWithPath("phone").description("전화번호"),
                        PayloadDocumentation.fieldWithPath("birthDate").description("생년월일"),
                        PayloadDocumentation.fieldWithPath("type").description("회원 유형 (CUSTOMER, PARTNER, OPERATOR)"),
                        PayloadDocumentation.fieldWithPath("roleIds").description("역할 ID 목록"),
                        PayloadDocumentation.fieldWithPath("permissionIds").description("권한 ID 목록")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("id").description("회원 ID")
                    )
                )
            )
    }

    @Test
    fun signIn() {
        every { authService.signIn(any()) } returns tokenResult

        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                          "email": "$email",
                          "password": "$password"
                        }
                    """.trimIndent()
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "sign-in",
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("email").description("계정"),
                        PayloadDocumentation.fieldWithPath("password").description("패스워드")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("accessToken").description("액세스 토큰"),
                        PayloadDocumentation.fieldWithPath("refreshToken").description("리프레시 토큰")
                    )
                )
            )
    }

    @Test
    fun refreshToken() {
        every { authService.refreshToken(any()) } returns tokenResult

        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/auth/token/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                          "accessToken": "$accessToken",
                          "refreshToken": "$refreshToken"
                        }
                    """.trimIndent()
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "token-refresh",
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("accessToken").description("액세스 토큰"),
                        PayloadDocumentation.fieldWithPath("refreshToken").description("리프레시 토큰")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("accessToken").description("액세스 토큰"),
                        PayloadDocumentation.fieldWithPath("refreshToken").description("리프레시 토큰")
                    )
                )
            )
    }

    @Test
    fun requestJoinVerify() {
        every { authService.requestJoinVerify(any()) } just runs

        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/auth/join/verify/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                          "email": "$email"
                        }
                    """.trimIndent()
                )
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent())
            .andDo(
                MockMvcRestDocumentation.document(
                    "join-verify-request",
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("email").description("이메일")
                    )
                )
            )
    }

    @Test
    fun confirmJoinVerify() {
        val token = "verifyToken123"
        every { authService.confirmJoinVerify(any()) } just runs

        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/auth/join/verify/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                          "token": "$token"
                        }
                    """.trimIndent()
                )
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent())
            .andDo(
                MockMvcRestDocumentation.document(
                    "join-verify-confirm",
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("token").description("토큰")
                    )
                )
            )
    }

    @Test
    fun requestPasswordReset() {
        every { authService.requestPasswordReset(any()) } just runs

        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/auth/password/reset/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                          "email": "$email"
                        }
                    """.trimIndent()
                )
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent())
            .andDo(
                MockMvcRestDocumentation.document(
                    "password-reset-request",
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("email").description("이메일")
                    )
                )
            )
    }

    @Test
    fun confirmPasswordReset() {
        val token = "resetToken123"
        val newPassword = "newPassword123"
        every { authService.confirmPasswordReset(any()) } just runs

        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/auth/password/reset/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                          "token": "$token",
                          "password": "$newPassword"
                        }
                    """.trimIndent()
                )
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent())
            .andDo(
                MockMvcRestDocumentation.document(
                    "password-reset-confirm",
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("token").description("토큰"),
                        PayloadDocumentation.fieldWithPath("password").description("비밀번호")
                    )
                )
            )
    }
}
