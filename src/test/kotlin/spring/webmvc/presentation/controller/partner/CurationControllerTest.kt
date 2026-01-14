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
import spring.webmvc.application.dto.result.CurationOffsetPageResult
import spring.webmvc.application.dto.result.CurationProductResult
import spring.webmvc.application.dto.result.CurationSummaryResult
import spring.webmvc.application.service.CurationService
import spring.webmvc.domain.model.entity.*
import spring.webmvc.domain.model.enums.CurationCategory
import spring.webmvc.domain.model.enums.ProductCategory
import spring.webmvc.domain.model.vo.ProductExposureProperty
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
                category = ProductCategory.ACCOMMODATION,
                name = "제주도 호텔",
                description = "제주도 3박4일",
                price = 100000L,
                quantity = 10L,
                exposureProperty = ProductExposureProperty(
                    isPromotional = false,
                    isNewArrival = false,
                    isFeatured = false,
                    isLowStock = false
                )
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
                category = ProductCategory.TRANSPORT,
                name = "부산 교통편",
                description = "부산 왕복 교통편",
                price = 200000L,
                quantity = 5L,
                exposureProperty = ProductExposureProperty(
                    isPromotional = false,
                    isNewArrival = false,
                    isFeatured = false,
                    isLowStock = false
                )
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
        val productResult = CurationProductResult(
            id = 1L,
            name = product1.product.name,
            description = product1.product.description,
            price = product1.product.price
        )

        val curationResult = CurationDetailResult(
            id = 1L,
            title = "인기상품",
            category = CurationCategory.HOME,
            products = listOf(productResult)
        )

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
                        PayloadDocumentation.fieldWithPath("category").description("큐레이션 카테고리"),
                        PayloadDocumentation.fieldWithPath("products[].id").description("상품 ID"),
                        PayloadDocumentation.fieldWithPath("products[].name").description("상품명"),
                        PayloadDocumentation.fieldWithPath("products[].description").description("상품 설명"),
                        PayloadDocumentation.fieldWithPath("products[].price").description("상품 가격")
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
                        PayloadDocumentation.fieldWithPath("size").description("큐레이션 수"),
                        PayloadDocumentation.fieldWithPath("curations[].id").description("큐레이션 ID"),
                        PayloadDocumentation.fieldWithPath("curations[].title").description("큐레이션 제목"),
                        PayloadDocumentation.fieldWithPath("curations[].category").description("큐레이션 카테고리")
                    )
                )
            )
    }

    @Test
    fun findCurationProduct() {
        val curationId = 1L
        val pageable = PageRequest.of(0, 10)

        val curationProduct1 = spyk(
            CurationProduct.create(
                curation = curation1,
                product = product1.product,
                sortOrder = 1L
            )
        ).apply { every { id } returns 1L }

        val curationProduct2 = spyk(
            CurationProduct.create(
                curation = curation1,
                product = product2.product,
                sortOrder = 2L
            )
        ).apply { every { id } returns 2L }

        val curationProducts = listOf(curationProduct1, curationProduct2)
        val page = PageImpl(curationProducts, pageable, curationProducts.size.toLong())

        val result = CurationOffsetPageResult.from(curation = curation1, page = page)

        every { curationService.findCurationProductWithOffsetPage(curationId, pageable) } returns result

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
                        PayloadDocumentation.fieldWithPath("id").description("큐레이션 ID"),
                        PayloadDocumentation.fieldWithPath("title").description("큐레이션 제목"),
                        PayloadDocumentation.fieldWithPath("category").description("큐레이션 카테고리"),
                        PayloadDocumentation.fieldWithPath("products.page").description("현재 페이지 번호"),
                        PayloadDocumentation.fieldWithPath("products.size").description("페이지 크기"),
                        PayloadDocumentation.fieldWithPath("products.totalElements").description("전체 요소 수"),
                        PayloadDocumentation.fieldWithPath("products.totalPages").description("전체 페이지 수"),
                        PayloadDocumentation.fieldWithPath("products.hasNext").description("다음 페이지 존재 여부"),
                        PayloadDocumentation.fieldWithPath("products.hasPrevious").description("이전 페이지 존재 여부"),
                        PayloadDocumentation.fieldWithPath("products.content[].id").description("상품 ID"),
                        PayloadDocumentation.fieldWithPath("products.content[].name").description("상품명"),
                        PayloadDocumentation.fieldWithPath("products.content[].description").description("상품 설명"),
                        PayloadDocumentation.fieldWithPath("products.content[].price").description("상품 가격")
                    )
                )
            )
    }
}
