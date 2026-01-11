package spring.webmvc.presentation.controller.partner

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.spyk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import spring.webmvc.application.dto.command.CurationCreateCommand
import spring.webmvc.application.dto.result.CurationDetailResult
import spring.webmvc.application.dto.result.CurationProductResult
import spring.webmvc.application.dto.result.CurationSummaryResult
import spring.webmvc.application.service.CurationService
import spring.webmvc.domain.model.entity.Accommodation
import spring.webmvc.domain.model.entity.Curation
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.model.entity.Transport
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.domain.model.enums.CurationCategory
import spring.webmvc.infrastructure.config.ControllerTest

@ControllerTest([CurationController::class])
class CurationControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var curationService: CurationService
    private lateinit var curation1: Curation
    private lateinit var product1: Accommodation
    private lateinit var product2: Transport

    @BeforeEach
    fun setUp() {
        curation1 = spyk(
            Curation.create(
                title = "여름 휴가 패키지",
                category = CurationCategory.HOME,
                isExposed = true,
                sortOrder = 1L
            )
        ).apply { every { id } returns 1L }

        val mockProduct1 = spyk(
            Product.create(
                category = Category.ACCOMMODATION,
                name = "제주도 호텔",
                description = "제주도 3박4일",
                price = 100000L,
                quantity = 10L
            )
        ).apply { every { id } returns 1L }

        product1 = spyk(
            Accommodation(
                product = mockProduct1,
                place = "제주도",
                checkInTime = java.time.Instant.now(),
                checkOutTime = java.time.Instant.now().plusSeconds(3600 * 24 * 4)
            )
        ).apply { every { id } returns 1L }

        val mockProduct2 = spyk(
            Product.create(
                category = Category.TRANSPORT,
                name = "부산 교통편",
                description = "부산 왕복 교통편",
                price = 200000L,
                quantity = 5L
            )
        ).apply { every { id } returns 2L }

        product2 = spyk(
            Transport(
                product = mockProduct2,
                departureLocation = "Seoul",
                arrivalLocation = "Busan",
                departureTime = java.time.Instant.now(),
                arrivalTime = java.time.Instant.now().plusSeconds(3600 * 2)
            )
        ).apply { every { id } returns 2L }
    }

    @Test
    fun createCuration() {
        val curationResult = CurationDetailResult.from(curation1)

        every { curationService.createCuration(any<CurationCreateCommand>()) } returns curationResult

        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/partner/curations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
					{
					  "title": "인기상품",
					  "category": "HOME",
					  "isExposed": true,
					  "sortOrder": 1,
					  "products": [
					    {
					      "productId": 1,
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
                    "partner-curation-create",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("title").description("큐레이션 제목"),
                        PayloadDocumentation.fieldWithPath("category").description("큐레이션 카테고리"),
                        PayloadDocumentation.fieldWithPath("isExposed").description("노출 여부"),
                        PayloadDocumentation.fieldWithPath("sortOrder").description("정렬 순서"),
                        PayloadDocumentation.fieldWithPath("products[].productId").description("상품 ID"),
                        PayloadDocumentation.fieldWithPath("products[].sortOrder").description("상품 정렬 순서")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("id").description("큐레이션 ID"),
                        PayloadDocumentation.fieldWithPath("title").description("큐레이션 제목"),
                        PayloadDocumentation.fieldWithPath("products").description("큐레이션 상품 목록")
                    )
                )
            )
    }

    @Test
    fun findCurations() {
        val category = CurationCategory.HOME
        val curationResult1 = CurationSummaryResult.from(curation1)

        val result = listOf(curationResult1)

        every { curationService.findCurations(category) } returns result

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/partner/curations")
                .queryParam("category", category.name)
                .header("Authorization", "Bearer access-token")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "partner-curation-list",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    RequestDocumentation.queryParameters(
                        RequestDocumentation.parameterWithName("category").description("큐레이션 카테고리")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("category").description("큐레이션 카테고리"),
                        PayloadDocumentation.fieldWithPath("size").description("큐레이션 수"),
                        PayloadDocumentation.fieldWithPath("curations[].id").description("큐레이션 ID"),
                        PayloadDocumentation.fieldWithPath("curations[].title").description("큐레이션 제목")
                    )
                )
            )
    }

    @Test
    fun findCurationProduct() {
        val curationId = 1L
        val pageable = PageRequest.of(0, 10)

        val curationProductResults = listOf(
            CurationProductResult(
                category = product1.product.category,
                name = product1.product.name,
                description = product1.product.description,
                price = product1.product.price
            ),
            CurationProductResult(
                category = product2.product.category,
                name = product2.product.name,
                description = product2.product.description,
                price = product2.product.price
            )
        )
        val page = PageImpl(curationProductResults, pageable, curationProductResults.size.toLong())

        every { curationService.findCurationProductWithOffsetPage(curationId, pageable) } returns page

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/partner/curations/{id}", curationId)
                .queryParam("page", "0")
                .queryParam("size", "10")
                .header("Authorization", "Bearer access-token")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "partner-curation-product",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("id").description("큐레이션 ID")
                    ),
                    RequestDocumentation.queryParameters(
                        RequestDocumentation.parameterWithName("page").description("페이지 번호").optional(),
                        RequestDocumentation.parameterWithName("size").description("페이지 크기").optional()
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("products[].category").description("상품 카테고리"),
                        PayloadDocumentation.fieldWithPath("products[].name").description("상품명"),
                        PayloadDocumentation.fieldWithPath("products[].description").description("상품 설명"),
                        PayloadDocumentation.fieldWithPath("products[].price").description("상품 가격"),

                        PayloadDocumentation.fieldWithPath("page.page").description("현재 페이지 번호"),
                        PayloadDocumentation.fieldWithPath("page.size").description("페이지 크기"),
                        PayloadDocumentation.fieldWithPath("page.totalElements").description("전체 요소 수"),
                        PayloadDocumentation.fieldWithPath("page.totalPages").description("전체 페이지 수"),
                        PayloadDocumentation.fieldWithPath("page.hasNext").description("다음 페이지 존재 여부"),
                        PayloadDocumentation.fieldWithPath("page.hasPrevious").description("이전 페이지 존재 여부")
                    )
                )
            )
    }
}
