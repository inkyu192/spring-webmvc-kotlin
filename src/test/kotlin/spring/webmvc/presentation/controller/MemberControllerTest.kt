package spring.webmvc.presentation.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.headers.HeaderDocumentation
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
import spring.webmvc.application.service.MemberService
import spring.webmvc.domain.model.entity.Member
import spring.webmvc.infrastructure.config.WebMvcTestConfig
import spring.webmvc.presentation.dto.request.MemberCreateRequest
import spring.webmvc.presentation.dto.request.MemberUpdateRequest
import java.time.Instant
import java.time.LocalDate


@WebMvcTest(MemberController::class)
@Import(WebMvcTestConfig::class)
@ExtendWith(RestDocumentationExtension::class)
class MemberControllerTest(
    @Autowired private val objectMapper: ObjectMapper,
) {
    @MockitoBean
    private lateinit var memberService: MemberService

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
    fun createMember() {
        val account = "test@gmail.com"
        val password = "password"
        val name = "name"
        val phone = "010-1234-1234"
        val birthDate = LocalDate.now()
        val roleIds = mutableListOf<Long>()
        val permissionIds = mutableListOf(1L)

        val request = MemberCreateRequest(
            account = account,
            password = password,
            name = name,
            phone = phone,
            birthDate = birthDate,
            roleIds = roleIds,
            permissionIds = permissionIds,
        )

        val member = Mockito.mock<Member>()
        Mockito.`when`(member.id).thenReturn(1L)
        Mockito.`when`(member.account).thenReturn(account)
        Mockito.`when`(member.name).thenReturn(name)
        Mockito.`when`(member.phone).thenReturn(phone)
        Mockito.`when`(member.birthDate).thenReturn(birthDate)
        Mockito.`when`(member.createdAt).thenReturn(Instant.now())

        Mockito.`when`(
            memberService.createMember(
                account = account,
                password = password,
                name = name,
                phone = phone,
                birthDate = birthDate,
                roleIds = roleIds,
                permissionIds = permissionIds,
            )
        ).thenReturn(member)

        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andDo(
                MockMvcRestDocumentation.document(
                    "member-create",
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("account").description("계정"),
                        PayloadDocumentation.fieldWithPath("password").description("패스워드"),
                        PayloadDocumentation.fieldWithPath("name").description("회원명"),
                        PayloadDocumentation.fieldWithPath("phone").description("번호"),
                        PayloadDocumentation.fieldWithPath("birthDate").description("생년월일"),
                        PayloadDocumentation.fieldWithPath("roleIds").description("역할목록"),
                        PayloadDocumentation.fieldWithPath("permissionIds").description("권한목록")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("id").description("아이디"),
                        PayloadDocumentation.fieldWithPath("account").description("계정"),
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
        val member = Mockito.mock<Member>()
        Mockito.`when`(member.id).thenReturn(1L)
        Mockito.`when`(member.account).thenReturn("account")
        Mockito.`when`(member.name).thenReturn("name")
        Mockito.`when`(member.phone).thenReturn("010-1234-1234")
        Mockito.`when`(member.birthDate).thenReturn(LocalDate.now())
        Mockito.`when`(member.createdAt).thenReturn(Instant.now())

        Mockito.`when`(memberService.findMember()).thenReturn(member)

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/members")
                .header("Authorization", "Bearer accessToken")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "member-get",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("id").description("아이디"),
                        PayloadDocumentation.fieldWithPath("account").description("계정"),
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
        val password = "password"
        val name = "name"
        val phone = "010-1234-1234"
        val birthDate = LocalDate.now()

        val request = MemberUpdateRequest(
            password = password,
            name = name,
            phone = phone,
            birthDate = birthDate,
        )
        val member = Mockito.mock<Member>()
        Mockito.`when`(member.id).thenReturn(1L)
        Mockito.`when`(member.account).thenReturn("account")
        Mockito.`when`(member.name).thenReturn(name)
        Mockito.`when`(member.phone).thenReturn(phone)
        Mockito.`when`(member.birthDate).thenReturn(birthDate)
        Mockito.`when`(member.createdAt).thenReturn(Instant.now())

        Mockito.`when`(
            memberService.updateMember(
                password = password,
                name = name,
                phone = phone,
                birthDate = birthDate,
            )
        ).thenReturn(member)

        mockMvc.perform(
            RestDocumentationRequestBuilders.patch("/members")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer accessToken")
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "member-update",
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
                        PayloadDocumentation.fieldWithPath("account").description("계정"),
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
        Mockito.doNothing().`when`(memberService).deleteMember()

        mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/members")
                .header("Authorization", "Bearer accessToken")
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent())
            .andDo(
                MockMvcRestDocumentation.document(
                    "member-delete",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    )
                )
            )
    }
}
