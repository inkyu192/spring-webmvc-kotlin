package spring.webmvc.presentation.controller

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import spring.webmvc.application.dto.command.CurationCreateCommand
import spring.webmvc.application.dto.result.CurationResult
import spring.webmvc.application.dto.result.ProductResult
import spring.webmvc.application.service.CurationService
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.infrastructure.config.WebMvcTestConfig
import java.time.Instant

@WebMvcTest(CurationController::class)
@Import(WebMvcTestConfig::class)
@ExtendWith(RestDocumentationExtension::class)
class CurationControllerTest() {
    @MockitoBean
    private lateinit var curationService: CurationService

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
    fun createCuration() {
        val id = 1L
        val title = "Test Curation"

        val curationResult = mock<CurationResult>()
        whenever(curationResult.id).thenReturn(id)
        whenever(curationResult.title).thenReturn(title)

        whenever(curationService.createCuration(any<CurationCreateCommand>())).thenReturn(curationResult)

        // When & Then
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/curations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
						{
						  "title": "$title",
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
                        PayloadDocumentation.fieldWithPath("products").description("상품 목록"),
                        PayloadDocumentation.fieldWithPath("products[].id").description("상품 ID"),
                        PayloadDocumentation.fieldWithPath("products[].sortOrder").description("상품 정렬 순서")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("id").description("큐레이션 ID"),
                        PayloadDocumentation.fieldWithPath("title").description("큐레이션 제목")
                    )
                )
            )
    }

    @Test
    fun findCurations() {
        val curationResult1 = mock<CurationResult>()
        whenever(curationResult1.id).thenReturn(1L)
        whenever(curationResult1.title).thenReturn("Curation 1")

        val curationResult2 = mock<CurationResult>()
        whenever(curationResult2.id).thenReturn(2L)
        whenever(curationResult2.title).thenReturn("Curation 2")

        val curationResults = listOf(curationResult1, curationResult2)

        whenever(curationService.findCurations()).thenReturn(curationResults)

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/curations")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "curation-list",
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("[].id").description("큐레이션 ID"),
                        PayloadDocumentation.fieldWithPath("[].title").description("큐레이션 제목")
                    )
                )
            )
    }

    @Test
    fun findCurationProduct() {
        val curationId = 1L
        val pageable: Pageable = PageRequest.of(0, 10)

        val productResult1 = mock<ProductResult>()
        whenever(productResult1.id).thenReturn(1L)
        whenever(productResult1.category).thenReturn(Category.ACCOMMODATION)
        whenever(productResult1.name).thenReturn("Product 1")
        whenever(productResult1.description).thenReturn("Description 1")
        whenever(productResult1.price).thenReturn(1000L)
        whenever(productResult1.quantity).thenReturn(10L)
        whenever(productResult1.createdAt).thenReturn(Instant.now())

        val productResult2 = mock<ProductResult>()
        whenever(productResult2.id).thenReturn(2L)
        whenever(productResult2.category).thenReturn(Category.FLIGHT)
        whenever(productResult2.name).thenReturn("Product 2")
        whenever(productResult2.description).thenReturn("Description 2")
        whenever(productResult2.price).thenReturn(2000L)
        whenever(productResult2.quantity).thenReturn(20L)
        whenever(productResult2.createdAt).thenReturn(Instant.now())

        val products = listOf(productResult1, productResult2)
        val productPage = PageImpl(products, pageable, products.size.toLong())

        whenever(curationService.findCurationProduct(pageable, curationId)).thenReturn(productPage)

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/curations/{id}", curationId)
                .queryParam("page", pageable.getPageNumber().toString())
                .queryParam("size", pageable.getPageSize().toString())
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "curation-product",
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("id").description("큐레이션 ID")
                    ),
                    RequestDocumentation.queryParameters(
                        RequestDocumentation.parameterWithName("page").description("페이지 번호").optional(),
                        RequestDocumentation.parameterWithName("size").description("페이지 크기").optional()
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("content[].id").description("상품 ID"),
                        PayloadDocumentation.fieldWithPath("content[].category").description("카테고리"),
                        PayloadDocumentation.fieldWithPath("content[].name").description("상품명"),
                        PayloadDocumentation.fieldWithPath("content[].description").description("설명"),
                        PayloadDocumentation.fieldWithPath("content[].price").description("가격"),
                        PayloadDocumentation.fieldWithPath("content[].quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("content[].createdAt").description("생성일시"),

                        PayloadDocumentation.fieldWithPath("pageable.pageNumber").description("현재 페이지 번호"),
                        PayloadDocumentation.fieldWithPath("pageable.pageSize").description("페이지 크기"),
                        PayloadDocumentation.fieldWithPath("pageable.offset").description("정렬 정보"),
                        PayloadDocumentation.fieldWithPath("pageable.paged").description("정렬 정보"),
                        PayloadDocumentation.fieldWithPath("pageable.unpaged").description("정렬 정보"),

                        PayloadDocumentation.fieldWithPath("pageable.sort.empty").description("정렬이 비어있는지 여부"),
                        PayloadDocumentation.fieldWithPath("pageable.sort.sorted").description("정렬되었는지 여부"),
                        PayloadDocumentation.fieldWithPath("pageable.sort.unsorted").description("정렬되지 않았는지 여부"),

                        PayloadDocumentation.fieldWithPath("last").description("마지막 페이지 여부"),
                        PayloadDocumentation.fieldWithPath("totalPages").description("전체 페이지 수"),
                        PayloadDocumentation.fieldWithPath("totalElements").description("전체 아이템 수"),
                        PayloadDocumentation.fieldWithPath("first").description("첫 페이지 여부"),
                        PayloadDocumentation.fieldWithPath("size").description("페이지 크기"),
                        PayloadDocumentation.fieldWithPath("number").description("현재 페이지 번호"),

                        PayloadDocumentation.fieldWithPath("sort.empty").description("정렬이 비어있는지 여부"),
                        PayloadDocumentation.fieldWithPath("sort.sorted").description("정렬되었는지 여부"),
                        PayloadDocumentation.fieldWithPath("sort.unsorted").description("정렬되지 않았는지 여부"),

                        PayloadDocumentation.fieldWithPath("numberOfElements").description("현재 페이지의 아이템 수"),
                        PayloadDocumentation.fieldWithPath("empty").description("빈 페이지 여부")
                    )
                )
            )
    }
}