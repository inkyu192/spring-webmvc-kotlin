package spring.webmvc.presentation.controller

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
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
import spring.webmvc.application.dto.result.AccommodationResult
import spring.webmvc.application.dto.result.FlightResult
import spring.webmvc.application.dto.result.ProductResult
import spring.webmvc.application.dto.result.TicketResult
import spring.webmvc.application.service.ProductService
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.infrastructure.config.WebMvcTestConfig
import java.time.Instant
import java.time.temporal.ChronoUnit

@WebMvcTest(ProductController::class)
@Import(WebMvcTestConfig::class)
@ExtendWith(RestDocumentationExtension::class)
class ProductControllerTest() {
    @MockitoBean
    private lateinit var productService: ProductService

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
    fun findProducts() {
        val pageable: Pageable = PageRequest.of(0, 10)
        val name = "name"
        val response = listOf(
            ProductResult(
                id = 1L,
                category = Category.ACCOMMODATION,
                name = "name1",
                description = "description",
                price = 1000,
                quantity = 10,
                createdAt = Instant.now(),
            ),
            ProductResult(
                id = 2L,
                category = Category.FLIGHT,
                name = "name2",
                description = "description",
                price = 2000,
                quantity = 20,
                createdAt = Instant.now(),
            ),
            ProductResult(
                id = 3L,
                category = Category.TICKET,
                name = "name3",
                description = "description",
                price = 3000,
                quantity = 30,
                createdAt = Instant.now(),
            ),
        )
        val page = PageImpl(response, pageable, response.size.toLong())

        Mockito.`when`(productService.findProducts(pageable = pageable, name = name)).thenReturn(page)

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/products")
                .header("Authorization", "Bearer access-token")
                .param("page", pageable.pageNumber.toString())
                .param("size", pageable.pageSize.toString())
                .param("name", name)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "product-list",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    RequestDocumentation.queryParameters(
                        RequestDocumentation.parameterWithName("page").description("페이지 번호").optional(),
                        RequestDocumentation.parameterWithName("size").description("페이지 크기").optional(),
                        RequestDocumentation.parameterWithName("name").description("상품명").optional()
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("content[].id").description("아이디"),
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

    @Test
    fun findTicket() {
        val productId = 1L
        val category = Category.TICKET
        val ticketResult = TicketResult(
            id = productId,
            name = "name",
            description = "description",
            price = 1000,
            quantity = 10, createdAt = Instant.now(),
            ticketId = 1L,
            place = "place",
            performanceTime = Instant.now(),
            duration = "duration",
            ageLimit = "ageLimit"
        )

        Mockito.`when`(productService.findProduct(id = productId, category = category)).thenReturn(ticketResult)

        // When & Then
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/products/{id}", productId)
                .header("Authorization", "Bearer access-token")
                .queryParam("category", category.toString())
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "product-ticket-get",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("id").description("아이디")
                    ),
                    RequestDocumentation.queryParameters(
                        RequestDocumentation.parameterWithName("category").description("카테고리")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("id").description("아이디"),
                        PayloadDocumentation.fieldWithPath("category").description("카테고리"),
                        PayloadDocumentation.fieldWithPath("name").description("티켓명"),
                        PayloadDocumentation.fieldWithPath("description").description("설명"),
                        PayloadDocumentation.fieldWithPath("price").description("가격"),
                        PayloadDocumentation.fieldWithPath("quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("createdAt").description("생성일시"),
                        PayloadDocumentation.fieldWithPath("ticketId").description("티켓아이디"),
                        PayloadDocumentation.fieldWithPath("place").description("장소"),
                        PayloadDocumentation.fieldWithPath("performanceTime").description("공연 시간"),
                        PayloadDocumentation.fieldWithPath("duration").description("공연 시간"),
                        PayloadDocumentation.fieldWithPath("ageLimit").description("관람 연령")
                    )
                )
            )
    }

    @Test
    fun findFlight() {
        val productId = 1L
        val category = Category.FLIGHT
        val flightResult = FlightResult(
            id = productId,
            name = "name",
            description = "description",
            price = 1000,
            quantity = 10,
            createdAt = Instant.now(),
            flightId = 1L,
            airline = "airline",
            flightNumber = "flightNumber",
            departureAirport = "departureAirport",
            arrivalAirport = "arrivalAirport",
            departureTime = Instant.now(),
            arrivalTime = Instant.now().plus(1, ChronoUnit.DAYS)
        )

        Mockito.`when`(productService.findProduct(id = productId, category = category)).thenReturn(flightResult)

        // When & Then
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/products/{id}", productId)
                .header("Authorization", "Bearer access-token")
                .queryParam("category", category.toString())
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "product-flight-get",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("id").description("아이디")
                    ),
                    RequestDocumentation.queryParameters(
                        RequestDocumentation.parameterWithName("category").description("카테고리")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("id").description("아이디"),
                        PayloadDocumentation.fieldWithPath("category").description("카테고리"),
                        PayloadDocumentation.fieldWithPath("name").description("티켓명"),
                        PayloadDocumentation.fieldWithPath("description").description("설명"),
                        PayloadDocumentation.fieldWithPath("price").description("가격"),
                        PayloadDocumentation.fieldWithPath("quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("createdAt").description("생성일시"),
                        PayloadDocumentation.fieldWithPath("flightId").description("항공아이디"),
                        PayloadDocumentation.fieldWithPath("airline").description("항공사"),
                        PayloadDocumentation.fieldWithPath("flightNumber").description("항공편 ID"),
                        PayloadDocumentation.fieldWithPath("departureAirport").description("출발 공항"),
                        PayloadDocumentation.fieldWithPath("arrivalAirport").description("도착 공항"),
                        PayloadDocumentation.fieldWithPath("departureTime").description("출발 시간"),
                        PayloadDocumentation.fieldWithPath("arrivalTime").description("도착 시간")
                    )
                )
            )
    }

    @Test
    fun findAccommodation() {
        val productId = 1L
        val category = Category.ACCOMMODATION
        val accommodationResult = AccommodationResult(
            id = productId,
            name = "name",
            description = "description",
            price = 1000,
            quantity = 10,
            createdAt = Instant.now(),
            accommodationId = 1L,
            place = "place",
            checkInTime = Instant.now(),
            checkOutTime = Instant.now().plus(1, ChronoUnit.DAYS)
        )

        Mockito.`when`(productService.findProduct(id = productId, category = category)).thenReturn(accommodationResult)

        // When & Then
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/products/{id}", productId)
                .header("Authorization", "Bearer access-token")
                .queryParam("category", category.toString())
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "product-accommodation-get",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("id").description("아이디")
                    ),
                    RequestDocumentation.queryParameters(
                        RequestDocumentation.parameterWithName("category").description("카테고리")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("id").description("아이디"),
                        PayloadDocumentation.fieldWithPath("category").description("카테고리"),
                        PayloadDocumentation.fieldWithPath("name").description("티켓명"),
                        PayloadDocumentation.fieldWithPath("description").description("설명"),
                        PayloadDocumentation.fieldWithPath("price").description("가격"),
                        PayloadDocumentation.fieldWithPath("quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("createdAt").description("생성일시"),
                        PayloadDocumentation.fieldWithPath("accommodationId").description("숙소아이디"),
                        PayloadDocumentation.fieldWithPath("place").description("장소"),
                        PayloadDocumentation.fieldWithPath("checkInTime").description("체크인 시간"),
                        PayloadDocumentation.fieldWithPath("checkOutTime").description("체크아웃 시간")
                    )
                )
            )
    }
}
