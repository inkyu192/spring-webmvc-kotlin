package spring.webmvc.presentation.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import spring.webmvc.application.dto.result.MenuResult
import spring.webmvc.application.service.MenuService
import spring.webmvc.infrastructure.config.ControllerTest

@ControllerTest([MenuController::class])
class MenuControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var menuService: MenuService
    private lateinit var menuResult1: MenuResult
    private lateinit var menuResult2: MenuResult

    @BeforeEach
    fun setUp() {
        val childMenu1 = MenuResult(
            id = 3L,
            name = "상품 관리",
            path = "/admin/products",
            children = emptyList()
        )

        val childMenu2 = MenuResult(
            id = 4L,
            name = "주문 관리",
            path = "/admin/orders",
            children = emptyList()
        )

        menuResult1 = MenuResult(
            id = 1L,
            name = "관리자",
            path = null,
            children = listOf(childMenu1, childMenu2)
        )

        menuResult2 = MenuResult(
            id = 2L,
            name = "대시보드",
            path = "/dashboard",
            children = emptyList()
        )
    }

    @Test
    fun findMenus() {
        val result = listOf(menuResult1, menuResult2)

        every { menuService.findMenus() } returns result

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/menus")
                .header("Authorization", "Bearer access-token")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "menu-list",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("[].id").description("메뉴 ID"),
                        PayloadDocumentation.fieldWithPath("[].name").description("메뉴명"),
                        PayloadDocumentation.fieldWithPath("[].path").description("메뉴 경로").optional(),
                        PayloadDocumentation.subsectionWithPath("[].children").description("하위 메뉴 목록")
                    )
                )
            )
    }
}