package spring.webmvc.presentation.controller

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.spy
import org.mockito.kotlin.whenever
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import spring.webmvc.application.dto.command.CurationCreateCommand
import spring.webmvc.application.dto.result.CurationProductResult
import spring.webmvc.application.dto.result.CurationResult
import spring.webmvc.application.service.CurationService
import spring.webmvc.domain.model.entity.Accommodation
import spring.webmvc.domain.model.entity.Curation
import spring.webmvc.domain.model.entity.Flight
import spring.webmvc.infrastructure.config.WebMvcTestConfig
import spring.webmvc.infrastructure.persistence.dto.CursorPage
import spring.webmvc.presentation.controller.support.MockMvcRestDocsSetup

@WebMvcTest(CurationController::class)
@Import(WebMvcTestConfig::class)
class CurationControllerTest() : MockMvcRestDocsSetup() {
    @MockitoBean
    private lateinit var curationService: CurationService
    private lateinit var curation1: Curation
    private lateinit var curation2: Curation
    private lateinit var product1: Accommodation
    private lateinit var product2: Flight

    @BeforeEach
    fun setUp() {
        curation1 = spy(
            Curation.create(
                title = "여름 휴가 패키지",
                isExposed = true,
                sortOrder = 1L
            )
        ).apply { whenever(id).thenReturn(1L) }

        curation2 = spy(
            Curation.create(
                title = "겨울 스키 패키지",
                isExposed = true,
                sortOrder = 2L
            )
        ).apply { whenever(id).thenReturn(2L) }

        product1 = spy(
            Accommodation.create(
                name = "제주도 호텔",
                description = "제주도 3박4일",
                price = 100000L,
                quantity = 10L,
                place = "제주도",
                checkInTime = java.time.Instant.now(),
                checkOutTime = java.time.Instant.now().plusSeconds(3600 * 24 * 4)
            )
        ).apply { whenever(id).thenReturn(1L) }

        product2 = spy(
            Flight.create(
                name = "부산 항공권",
                description = "부산 왕복 항공권",
                price = 200000L,
                quantity = 5L,
                airline = "Korean Air",
                flightNumber = "KE1234",
                departureAirport = "ICN",
                arrivalAirport = "PUS",
                departureTime = java.time.Instant.now(),
                arrivalTime = java.time.Instant.now().plusSeconds(3600 * 2)
            )
        ).apply { whenever(id).thenReturn(2L) }
    }

    @Test
    fun createCuration() {
        val id = 1L

        whenever(curationService.createCuration(any<CurationCreateCommand>())).thenReturn(id)

        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/curations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
						{
						  "title": "인기상품",
						  "isExposed": true,
						  "sortOrder": 1,
						  "products": [
						    {
						      "id": 1,
						      "sortOrder": 1
						    }
						  ]
						}
						
						""".trimIndent()
                )
                .header("Authorization", "Bearer access-token")
        )
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andDo(
                MockMvcRestDocumentation.document(
                    "curation-create",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("title").description("큐레이션 제목"),
                        PayloadDocumentation.fieldWithPath("isExposed").description("노출 여부"),
                        PayloadDocumentation.fieldWithPath("sortOrder").description("정렬 순서"),
                        PayloadDocumentation.fieldWithPath("products[].id").description("상품 ID"),
                        PayloadDocumentation.fieldWithPath("products[].sortOrder").description("상품 정렬 순서")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("id").description("큐레이션 ID")
                    )
                )
            )
    }

    @Test
    fun findCurations() {
        val curationResult1 = CurationResult(curation1)
        val curationResult2 = CurationResult(curation2)

        val result = listOf(curationResult1, curationResult2)

        whenever(curationService.findCurations()).thenReturn(result)

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/curations")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "curation-list",
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("count").description("큐레이션 수"),
                        PayloadDocumentation.fieldWithPath("curations[].id").description("큐레이션 ID"),
                        PayloadDocumentation.fieldWithPath("curations[].title").description("큐레이션 제목"),
                    )
                )
            )
    }

    @Test
    fun findCurationProduct() {
        val curationId = 1L
        val size = 10
        val cursorId = null

        val cursorPage = CursorPage(listOf(product1, product2), size, false, null)
        val curationProductResult = CurationProductResult(curation1, cursorPage)

        whenever(curationService.findCurationProduct(curationId, cursorId, size)).thenReturn(curationProductResult)

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/curations/{id}", curationId)
                .queryParam("size", size.toString())
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "curation-product",
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("id").description("큐레이션 ID")
                    ),
                    RequestDocumentation.queryParameters(
                        RequestDocumentation.parameterWithName("size").description("페이지 크기").optional(),
                        RequestDocumentation.parameterWithName("cursorId").description("커서 ID").optional()
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("id").description("큐레이션 ID"),
                        PayloadDocumentation.fieldWithPath("title").description("큐레이션 제목"),
                        PayloadDocumentation.fieldWithPath("page.size").description("페이지 크기"),
                        PayloadDocumentation.fieldWithPath("page.hasNext").description("다음 페이지 존재 여부"),
                        PayloadDocumentation.fieldWithPath("page.nextCursorId").description("다음 커서 ID"),
                        PayloadDocumentation.fieldWithPath("products[].id").description("상품 ID"),
                        PayloadDocumentation.fieldWithPath("products[].category").description("상품 카테고리"),
                        PayloadDocumentation.fieldWithPath("products[].name").description("상품명"),
                        PayloadDocumentation.fieldWithPath("products[].description").description("상품 설명"),
                        PayloadDocumentation.fieldWithPath("products[].price").description("상품 가격"),
                        PayloadDocumentation.fieldWithPath("products[].quantity").description("상품 수량"),
                        PayloadDocumentation.fieldWithPath("products[].createdAt").description("상품 생성일시")
                    )
                )
            )
    }
}