package spring.webmvc.presentation.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import spring.webmvc.application.dto.result.UserDetail
import spring.webmvc.application.service.UserService
import spring.webmvc.domain.model.entity.User
import spring.webmvc.domain.model.entity.UserCredential
import spring.webmvc.domain.model.enums.Gender
import spring.webmvc.domain.model.vo.Email
import spring.webmvc.domain.model.vo.Phone
import spring.webmvc.infrastructure.config.WebMvcTestConfig
import spring.webmvc.presentation.controller.support.MockMvcRestDocsSetup
import java.time.Instant
import java.time.LocalDate


@WebMvcTest(UserController::class)
@Import(WebMvcTestConfig::class)
class UserControllerTest : MockMvcRestDocsSetup() {
    @MockkBean
    private lateinit var userService: UserService
    private lateinit var user: User
    private lateinit var userCredential: UserCredential
    private lateinit var userDetail: UserDetail
    private lateinit var email: String
    private lateinit var name: String
    private lateinit var phone: String
    private lateinit var birthday: LocalDate
    private lateinit var gender: Gender

    @BeforeEach
    fun beforeEach() {
        val authentication = UsernamePasswordAuthenticationToken(
            1L,
            null,
            listOf(
                SimpleGrantedAuthority("USER_READ"),
                SimpleGrantedAuthority("USER_WRITE"),
            ),
        )

        SecurityContextHolder.getContext().authentication = authentication

        email = "test@gmail.com"
        name = "홍길동"
        phone = "010-1234-1234"
        birthday = LocalDate.of(1990, 1, 1)
        gender = Gender.MALE

        user = mockk<User>()
        every { user.id } returns 1L
        every { user.name } returns name
        every { user.phone } returns Phone.create(phone)
        every { user.birthday } returns birthday
        every { user.gender } returns gender
        every { user.createdAt } returns Instant.now()

        userCredential = mockk<UserCredential>()
        every { userCredential.email } returns Email.create(email)
        every { userCredential.verifiedAt } returns Instant.now()

        userDetail = UserDetail(
            user = user,
            credential = userCredential,
            oauths = emptyList(),
        )
    }

    @AfterEach
    fun afterEach() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun findUsers() {
        val page = PageImpl(listOf(user), PageRequest.of(0, 20), 1)
        every { userService.findUsers(any()) } returns page

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/users")
                .header("Authorization", "Bearer accessToken")
                .param("createdFrom", "2024-01-01T00:00:00Z")
                .param("createdTo", "2024-12-31T23:59:59Z")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "user-list",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    RequestDocumentation.queryParameters(
                        RequestDocumentation.parameterWithName("page").description("페이지 번호").optional(),
                        RequestDocumentation.parameterWithName("size").description("페이지 크기").optional(),
                        RequestDocumentation.parameterWithName("email").description("이메일").optional(),
                        RequestDocumentation.parameterWithName("phone").description("전화번호").optional(),
                        RequestDocumentation.parameterWithName("name").description("이름").optional(),
                        RequestDocumentation.parameterWithName("status").description("상태").optional(),
                        RequestDocumentation.parameterWithName("createdFrom").description("생성일 시작"),
                        RequestDocumentation.parameterWithName("createdTo").description("생성일 종료"),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("users[].id").description("아이디"),
                        PayloadDocumentation.fieldWithPath("users[].name").description("회원명"),
                        PayloadDocumentation.fieldWithPath("users[].phone").description("번호"),
                        PayloadDocumentation.fieldWithPath("users[].gender").description("성별"),
                        PayloadDocumentation.fieldWithPath("users[].birthday").description("생년월일"),
                        PayloadDocumentation.fieldWithPath("users[].createdAt").description("생성일시"),
                        PayloadDocumentation.fieldWithPath("page.page").description("현재 페이지"),
                        PayloadDocumentation.fieldWithPath("page.size").description("페이지 크기"),
                        PayloadDocumentation.fieldWithPath("page.totalElements").description("전체 요소 수"),
                        PayloadDocumentation.fieldWithPath("page.totalPages").description("전체 페이지 수"),
                        PayloadDocumentation.fieldWithPath("page.hasNext").description("다음 페이지 여부"),
                        PayloadDocumentation.fieldWithPath("page.hasPrevious").description("이전 페이지 여부"),
                    )
                )
            )
    }

    @Test
    fun findUser() {
        every { userService.findUserDetail(1L) } returns userDetail

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/users/{id}", 1L)
                .header("Authorization", "Bearer accessToken")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "user-detail",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("id").description("회원 아이디")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("id").description("아이디"),
                        PayloadDocumentation.fieldWithPath("name").description("회원명"),
                        PayloadDocumentation.fieldWithPath("phone").description("번호"),
                        PayloadDocumentation.fieldWithPath("gender").description("성별"),
                        PayloadDocumentation.fieldWithPath("birthday").description("생년월일"),
                        PayloadDocumentation.fieldWithPath("credential").description("인증 정보").optional(),
                        PayloadDocumentation.fieldWithPath("credential.email").description("이메일").optional(),
                        PayloadDocumentation.fieldWithPath("credential.verifiedAt").description("인증 일시").optional(),
                        PayloadDocumentation.fieldWithPath("oauths").description("OAuth 목록"),
                        PayloadDocumentation.fieldWithPath("createdAt").description("생성일시")
                    )
                )
            )
    }
}