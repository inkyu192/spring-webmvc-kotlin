package spring.webmvc.presentation.controller.customer

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import spring.webmvc.application.service.MemberService
import spring.webmvc.domain.model.entity.Member
import spring.webmvc.domain.model.vo.Email
import spring.webmvc.domain.model.vo.Phone
import spring.webmvc.infrastructure.config.WebMvcTestConfig
import spring.webmvc.presentation.controller.support.MockMvcRestDocsSetup
import java.time.Instant
import java.time.LocalDate


@WebMvcTest(CustomerMemberController::class)
@Import(WebMvcTestConfig::class)
class CustomerMemberControllerTest : MockMvcRestDocsSetup() {
    @MockkBean
    private lateinit var memberService: MemberService
    private lateinit var member: Member
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var name: String
    private lateinit var phone: String
    private lateinit var birthDate: LocalDate
    private lateinit var roleIds: MutableList<Long>
    private lateinit var permissionIds: MutableList<Long>

    @BeforeEach
    fun beforeEach() {
        val authentication = UsernamePasswordAuthenticationToken(
            1L,
            null,
            listOf(SimpleGrantedAuthority("TEST")),
        )

        SecurityContextHolder.getContext().authentication = authentication

        email = "test@gmail.com"
        password = "password"
        name = "name"
        phone = "010-1234-1234"
        birthDate = LocalDate.now()
        roleIds = mutableListOf()
        permissionIds = mutableListOf(1L)
        member = mockk<Member>()
        every { member.id } returns 1L
        every { member.email } returns Email.create(email)
        every { member.name } returns name
        every { member.phone } returns Phone.create(phone)
        every { member.birthDate } returns birthDate
        every { member.createdAt } returns Instant.now()
    }

    @AfterEach
    fun afterEach() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun createMember() {
        every { memberService.createMember(command = any()) } returns member

        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/customer/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                          "email": "$email",
                          "password": "$password",
                          "name": "$name",
                          "phone": "$phone",
                          "birthDate": "$birthDate",
                          "roleIds": $roleIds,
                          "permissionIds": $permissionIds
                        }
                    """.trimIndent()
                )
        )
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andDo(
                MockMvcRestDocumentation.document(
                    "customer-member-create",
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("email").description("계정"),
                        PayloadDocumentation.fieldWithPath("password").description("패스워드"),
                        PayloadDocumentation.fieldWithPath("name").description("회원명"),
                        PayloadDocumentation.fieldWithPath("phone").description("번호"),
                        PayloadDocumentation.fieldWithPath("birthDate").description("생년월일"),
                        PayloadDocumentation.fieldWithPath("roleIds").description("역할목록"),
                        PayloadDocumentation.fieldWithPath("permissionIds").description("권한목록")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("id").description("아이디"),
                        PayloadDocumentation.fieldWithPath("email").description("계정"),
                        PayloadDocumentation.fieldWithPath("name").description("회원명"),
                        PayloadDocumentation.fieldWithPath("phone").description("번호"),
                        PayloadDocumentation.fieldWithPath("birthDate").description("생년월일"),
                        PayloadDocumentation.fieldWithPath("createdAt").description("생성일시")
                    )
                )
            )
    }

    @Test
    fun findMember() {
        every { memberService.findMember(any()) } returns member

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/customer/members")
                .header("Authorization", "Bearer accessToken")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "customer-member-get",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("id").description("아이디"),
                        PayloadDocumentation.fieldWithPath("email").description("계정"),
                        PayloadDocumentation.fieldWithPath("name").description("회원명"),
                        PayloadDocumentation.fieldWithPath("phone").description("번호"),
                        PayloadDocumentation.fieldWithPath("birthDate").description("생년월일"),
                        PayloadDocumentation.fieldWithPath("createdAt").description("생성일시")
                    )
                )
            )
    }

    @Test
    fun updateMember() {
        every { memberService.updateMember(command = any()) } returns member

        mockMvc.perform(
            RestDocumentationRequestBuilders.patch("/customer/members")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer accessToken")
                .content(
                    """
                        {
                          "password": "$password",
                          "name": "$name",
                          "phone": "$phone",
                          "birthDate": "$birthDate"
                        }
                    """.trimIndent()
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "customer-member-update",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("password").description("패스워드"),
                        PayloadDocumentation.fieldWithPath("name").description("회원명"),
                        PayloadDocumentation.fieldWithPath("phone").description("번호"),
                        PayloadDocumentation.fieldWithPath("birthDate").description("생년월일")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("id").description("아이디"),
                        PayloadDocumentation.fieldWithPath("email").description("계정"),
                        PayloadDocumentation.fieldWithPath("name").description("회원명"),
                        PayloadDocumentation.fieldWithPath("phone").description("번호"),
                        PayloadDocumentation.fieldWithPath("birthDate").description("생년월일"),
                        PayloadDocumentation.fieldWithPath("createdAt").description("생성일시")
                    )
                )
            )
    }

    @Test
    fun deleteMember() {
        every { memberService.updateMemberStatus(any()) } returns member

        mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/customer/members")
                .header("Authorization", "Bearer accessToken")
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent())
            .andDo(
                MockMvcRestDocumentation.document(
                    "customer-member-delete",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    )
                )
            )
    }

    @Test
    fun updatePassword() {
        every { memberService.updatePassword(any()) } just runs

        mockMvc.perform(
            RestDocumentationRequestBuilders.patch("/customer/members/password")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer accessToken")
                .content(
                    """
                        {
                          "currentPassword": "currentPassword",
                          "newPassword": "newPassword"
                        }
                    """.trimIndent()
                )
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent())
            .andDo(
                MockMvcRestDocumentation.document(
                    "customer-member-password-update",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("currentPassword").description("현재 패스워드"),
                        PayloadDocumentation.fieldWithPath("newPassword").description("새 패스워드")
                    )
                )
            )
    }
}