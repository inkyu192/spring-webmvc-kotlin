package spring.webmvc.presentation.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import spring.webmvc.application.dto.result.CodeGroupResult
import spring.webmvc.application.dto.result.CodeResult
import spring.webmvc.application.service.CodeService
import spring.webmvc.infrastructure.config.ControllerTest

@ControllerTest([CodeController::class])
class CodeControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var codeService: CodeService

    private lateinit var codeGroupResults: List<CodeGroupResult>

    @BeforeEach
    fun setUp() {
        codeGroupResults = listOf(
            CodeGroupResult(
                name = "Gender",
                label = "성별",
                codes = listOf(
                    CodeResult(code = "MALE", label = "남성"),
                    CodeResult(code = "FEMALE", label = "여성"),
                ),
            ),
            CodeGroupResult(
                name = "OrderStatus",
                label = "주문 상태",
                codes = listOf(
                    CodeResult(code = "ORDERED", label = "주문완료"),
                    CodeResult(code = "CANCELLED", label = "주문취소"),
                ),
            ),
        )
    }

    @Test
    fun findCodes() {
        every { codeService.findCodes() } returns codeGroupResults

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/codes")
                .header("Accept-Language", "ko")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "code-list",
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("size").description("코드 그룹 수"),
                        PayloadDocumentation.fieldWithPath("codeGroups[].name").description("코드 그룹명"),
                        PayloadDocumentation.fieldWithPath("codeGroups[].label").description("코드 그룹 라벨 (번역)"),
                        PayloadDocumentation.fieldWithPath("codeGroups[].codes[].code").description("코드"),
                        PayloadDocumentation.fieldWithPath("codeGroups[].codes[].label").description("코드 라벨 (번역)"),
                    ),
                )
            )
    }
}
