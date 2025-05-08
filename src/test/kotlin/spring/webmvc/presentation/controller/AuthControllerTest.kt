package spring.webmvc.presentation.controller

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import spring.webmvc.application.service.AuthService
import spring.webmvc.infrastructure.config.WebMvcTestConfig

@WebMvcTest(AuthController::class)
@Import(WebMvcTestConfig::class)
@ExtendWith(RestDocumentationExtension::class)
class AuthControllerTest() {
    @MockitoBean
    private lateinit var authService: AuthService

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp(webApplicationContext: WebApplicationContext, restDocumentation: RestDocumentationContextProvider) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply<DefaultMockMvcBuilder>(
                MockMvcRestDocumentation.documentationConfiguration(restDocumentation)
                    .operationPreprocessors()
                    .withRequestDefaults(Preprocessors.prettyPrint())
                    .withResponseDefaults(Preprocessors.prettyPrint())
            )
            .build()
    }

    @Test
    fun login() {
        val accessToken = "accessToken"
        val refreshToken = "refreshToken"
        val account = "test@gmail.com"
        val password = "password"

        val pair = Pair(accessToken, refreshToken)

        Mockito.`when`(authService.login(account = account, password = password)).thenReturn(pair)

        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                          "account": "$account",
                          "password": "$password"
                        }
                    """.trimIndent()
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "login",
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("account").description("계정"),
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
        val accessToken = "accessToken"
        val refreshToken = "refreshToken"

        val pair = Pair(accessToken, refreshToken)

        Mockito.`when`(authService.refreshToken(accessToken = accessToken, refreshToken = refreshToken))
            .thenReturn(pair)

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
}
