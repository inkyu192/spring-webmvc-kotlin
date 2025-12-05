package spring.webmvc.presentation.controller.operator

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
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
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


@WebMvcTest(OperatorMemberController::class)
@Import(WebMvcTestConfig::class)
class OperatorMemberControllerTest : MockMvcRestDocsSetup() {
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
            listOf(
                SimpleGrantedAuthority("OPERATOR_MEMBER_READ"),
                SimpleGrantedAuthority("OPERATOR_MEMBER_WRITE"),
            ),
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
            RestDocumentationRequestBuilders.post("/operator/members")
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
                    "operator-member-create",
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
    fun findMembers() {
        val page = PageImpl(listOf(member), PageRequest.of(0, 20), 1)
        every { memberService.findMembers(any()) } returns page

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/operator/members")
                .header("Authorization", "Bearer accessToken")
                .param("createdFrom", "2024-01-01T00:00:00Z")
                .param("createdTo", "2024-12-31T23:59:59Z")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "operator-member-list",
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
                        RequestDocumentation.parameterWithName("createdFrom").description("생성일 시작").optional(),
                        RequestDocumentation.parameterWithName("createdTo").description("생성일 종료").optional(),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("members[].id").description("아이디"),
                        PayloadDocumentation.fieldWithPath("members[].email").description("계정"),
                        PayloadDocumentation.fieldWithPath("members[].name").description("회원명"),
                        PayloadDocumentation.fieldWithPath("members[].phone").description("번호"),
                        PayloadDocumentation.fieldWithPath("members[].birthDate").description("생년월일"),
                        PayloadDocumentation.fieldWithPath("members[].createdAt").description("생성일시"),
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
    fun findMember() {
        every { memberService.findMember(1L) } returns member

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/operator/members/{id}", 1L)
                .header("Authorization", "Bearer accessToken")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "operator-member-get",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("id").description("회원 아이디")
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
    fun updateMemberStatus() {
        every { memberService.updateMemberStatus(any()) } returns member

        mockMvc.perform(
            RestDocumentationRequestBuilders.patch("/operator/members/{id}/status", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer accessToken")
                .content(
                    """
                        {
                          "status": "SUSPENDED"
                        }
                    """.trimIndent()
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "operator-member-status-update",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("id").description("회원 아이디")
                    ),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("status").description("변경할 상태")
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
}
