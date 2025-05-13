package spring.webmvc.presentation.controller

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.*
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
import spring.webmvc.application.dto.command.*
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

        whenever(productService.findProducts(pageable = pageable, name = name)).thenReturn(page)

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

        whenever(productService.findProduct(id = productId, category = category)).thenReturn(ticketResult)

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/products/{id}", productId)
                .header("Authorization", "Bearer access-token")
                .queryParam("category", category.toString())
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "ticket-get",
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

        whenever(productService.findProduct(id = productId, category = category)).thenReturn(flightResult)

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/products/{id}", productId)
                .header("Authorization", "Bearer access-token")
                .queryParam("category", category.toString())
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "flight-get",
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

        whenever(productService.findProduct(id = productId, category = category)).thenReturn(accommodationResult)

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/products/{id}", productId)
                .header("Authorization", "Bearer access-token")
                .queryParam("category", category.toString())
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "accommodation-get",
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

    @Test
    fun createTicket() {
        val productId = 1L
        val category = Category.TICKET
        val name = "name"
        val description = "description"
        val price = 1000
        val quantity = 5
        val place = "place"
        val performanceTime = Instant.now()
        val duration = "duration"
        val ageLimit = "ageLimit"
        val createdAt = Instant.now()

        val ticketResult = mock<TicketResult>()
        whenever(ticketResult.id).thenReturn(productId)
        whenever(ticketResult.category).thenReturn(category)
        whenever(ticketResult.name).thenReturn(name)
        whenever(ticketResult.description).thenReturn(description)
        whenever(ticketResult.price).thenReturn(price)
        whenever(ticketResult.quantity).thenReturn(quantity)
        whenever(ticketResult.place).thenReturn(place)
        whenever(ticketResult.performanceTime).thenReturn(performanceTime)
        whenever(ticketResult.duration).thenReturn(duration)
        whenever(ticketResult.ageLimit).thenReturn(ageLimit)
        whenever(ticketResult.createdAt).thenReturn(createdAt)

        whenever(productService.createProduct(any<TicketCreateCommand>())).thenReturn(ticketResult)

        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
						{
						  "category": "$category",
						  "name": "$name",
						  "description": "$description",
						  "price": $price,
						  "quantity": $quantity,
						  "place": "$place",
						  "performanceTime": "$performanceTime",
						  "duration": "$duration",
						  "ageLimit": "$ageLimit"
						}
						
						""".trimIndent()
                )
                .header("Authorization", "Bearer access-token")
        )
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andDo(
                MockMvcRestDocumentation.document(
                    "ticket-create",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("category").description("카테고리"),
                        PayloadDocumentation.fieldWithPath("name").description("티켓명"),
                        PayloadDocumentation.fieldWithPath("description").description("설명"),
                        PayloadDocumentation.fieldWithPath("price").description("가격"),
                        PayloadDocumentation.fieldWithPath("quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("place").description("장소"),
                        PayloadDocumentation.fieldWithPath("performanceTime").description("공연 시간"),
                        PayloadDocumentation.fieldWithPath("duration").description("공연 시간"),
                        PayloadDocumentation.fieldWithPath("ageLimit").description("관람 연령")
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
    fun createFlight() {
        val productId = 1L
        val category = Category.FLIGHT
        val name = "name"
        val description = "description"
        val price = 1000
        val quantity = 5
        val airline = "airline"
        val flightNumber = "flightNumber"
        val departureAirport = "departureAirport"
        val arrivalAirport = "arrivalAirport"
        val departureTime = Instant.now()
        val arrivalTime = Instant.now().plus(1, ChronoUnit.HOURS)
        val createdAt = Instant.now()

        val flightResult = mock<FlightResult>()
        whenever(flightResult.id).thenReturn(productId)
        whenever(flightResult.category).thenReturn(category)
        whenever(flightResult.name).thenReturn(name)
        whenever(flightResult.description).thenReturn(description)
        whenever(flightResult.price).thenReturn(price)
        whenever(flightResult.quantity).thenReturn(quantity)
        whenever(flightResult.airline).thenReturn(airline)
        whenever(flightResult.flightNumber).thenReturn(flightNumber)
        whenever(flightResult.departureAirport).thenReturn(departureAirport)
        whenever(flightResult.arrivalAirport).thenReturn(arrivalAirport)
        whenever(flightResult.departureTime).thenReturn(departureTime)
        whenever(flightResult.arrivalTime).thenReturn(arrivalTime)
        whenever(flightResult.createdAt).thenReturn(createdAt)

        whenever(productService.createProduct(any<FlightCreateCommand>())).thenReturn(flightResult)

        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
						{
						  "category": "$category",
						  "name": "$name",
						  "description": "$description",
						  "price": $price,
						  "quantity": $quantity,
						  "airline": "$airline",
						  "flightNumber": "$flightNumber",
						  "departureAirport": "$departureAirport",
						  "arrivalAirport": "$arrivalAirport",
						  "departureTime": "$departureTime",
						  "arrivalTime": "$arrivalTime"
						}
						
						""".trimIndent()
                )
                .header("Authorization", "Bearer access-token")
        )
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andDo(
                MockMvcRestDocumentation.document(
                    "flight-create",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("category").description("카테고리"),
                        PayloadDocumentation.fieldWithPath("name").description("항공편명"),
                        PayloadDocumentation.fieldWithPath("description").description("설명"),
                        PayloadDocumentation.fieldWithPath("price").description("가격"),
                        PayloadDocumentation.fieldWithPath("quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("airline").description("항공사"),
                        PayloadDocumentation.fieldWithPath("flightNumber").description("항공편 ID"),
                        PayloadDocumentation.fieldWithPath("departureAirport").description("출발 공항"),
                        PayloadDocumentation.fieldWithPath("arrivalAirport").description("도착 공항"),
                        PayloadDocumentation.fieldWithPath("departureTime").description("출발 시간"),
                        PayloadDocumentation.fieldWithPath("arrivalTime").description("도착 시간")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("id").description("아이디"),
                        PayloadDocumentation.fieldWithPath("category").description("카테고리"),
                        PayloadDocumentation.fieldWithPath("name").description("항공편명"),
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
    fun createAccommodation() {
        val productId = 1L
        val category = Category.ACCOMMODATION
        val name = "name"
        val description = "description"
        val price = 1000
        val quantity = 5
        val place = "place"
        val checkInTime = Instant.now()
        val checkOutTime = Instant.now().plus(1, ChronoUnit.DAYS)
        val createdAt = Instant.now()

        val accommodationResult = mock<AccommodationResult>()
        whenever(accommodationResult.id).thenReturn(productId)
        whenever(accommodationResult.category).thenReturn(category)
        whenever(accommodationResult.name).thenReturn(name)
        whenever(accommodationResult.description).thenReturn(description)
        whenever(accommodationResult.price).thenReturn(price)
        whenever(accommodationResult.quantity).thenReturn(quantity)
        whenever(accommodationResult.place).thenReturn(place)
        whenever(accommodationResult.checkInTime).thenReturn(checkInTime)
        whenever(accommodationResult.checkOutTime).thenReturn(checkOutTime)
        whenever(accommodationResult.createdAt).thenReturn(createdAt)

        whenever(productService.createProduct(any<AccommodationCreateCommand>())).thenReturn(accommodationResult)

        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
						{
						  "category": "$category",
						  "name": "$name",
						  "description": "$description",
						  "price": $price,
						  "quantity": $quantity,
						  "place": "$place",
						  "checkInTime": "$checkInTime",
						  "checkOutTime": "$checkOutTime"
						}
						
						""".trimIndent()
                )
                .header("Authorization", "Bearer access-token")
        )
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andDo(
                MockMvcRestDocumentation.document(
                    "accommodation-create",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("category").description("카테고리"),
                        PayloadDocumentation.fieldWithPath("name").description("숙소명"),
                        PayloadDocumentation.fieldWithPath("description").description("설명"),
                        PayloadDocumentation.fieldWithPath("price").description("가격"),
                        PayloadDocumentation.fieldWithPath("quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("place").description("장소"),
                        PayloadDocumentation.fieldWithPath("checkInTime").description("체크인 시간"),
                        PayloadDocumentation.fieldWithPath("checkOutTime").description("체크아웃 시간")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("id").description("아이디"),
                        PayloadDocumentation.fieldWithPath("category").description("카테고리"),
                        PayloadDocumentation.fieldWithPath("name").description("숙소명"),
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

    @Test
    fun updateTicket() {
        val productId = 1L
        val category = Category.TICKET
        val name = "name"
        val description = "description"
        val price = 1000
        val quantity = 5
        val place = "place"
        val performanceTime = Instant.now()
        val duration = "duration"
        val ageLimit = "ageLimit"
        val createdAt = Instant.now()

        val ticketResult = mock<TicketResult>()
        whenever(ticketResult.id).thenReturn(productId)
        whenever(ticketResult.category).thenReturn(category)
        whenever(ticketResult.name).thenReturn(name)
        whenever(ticketResult.description).thenReturn(description)
        whenever(ticketResult.price).thenReturn(price)
        whenever(ticketResult.quantity).thenReturn(quantity)
        whenever(ticketResult.place).thenReturn(place)
        whenever(ticketResult.performanceTime).thenReturn(performanceTime)
        whenever(ticketResult.duration).thenReturn(duration)
        whenever(ticketResult.ageLimit).thenReturn(ageLimit)
        whenever(ticketResult.createdAt).thenReturn(createdAt)

        whenever(
            productService.updateProduct(
                id = eq(productId),
                productUpdateCommand = any<TicketUpdateCommand>()
            )
        ).thenReturn(ticketResult)

        mockMvc.perform(
            RestDocumentationRequestBuilders.patch("/products/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
						{
						  "category": "$category",
						  "name": "$name",
						  "description": "$description",
						  "price": $price,
						  "quantity": $quantity,
						  "place": "$place",
						  "performanceTime": "$performanceTime",
						  "duration": "$duration",
						  "ageLimit": "$ageLimit"
						}
						
						""".trimIndent()
                )
                .header("Authorization", "Bearer access-token")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "ticket-update",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("category").description("카테고리"),
                        PayloadDocumentation.fieldWithPath("name").description("티켓명"),
                        PayloadDocumentation.fieldWithPath("description").description("설명"),
                        PayloadDocumentation.fieldWithPath("price").description("가격"),
                        PayloadDocumentation.fieldWithPath("quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("place").description("장소"),
                        PayloadDocumentation.fieldWithPath("performanceTime").description("공연 시간"),
                        PayloadDocumentation.fieldWithPath("duration").description("공연 시간"),
                        PayloadDocumentation.fieldWithPath("ageLimit").description("관람 연령")
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
    fun updateFlight() {
        val productId = 1L
        val category = Category.FLIGHT
        val name = "name"
        val description = "description"
        val price = 1000
        val quantity = 5
        val airline = "airline"
        val flightNumber = "flightNumber"
        val departureAirport = "departureAirport"
        val arrivalAirport = "arrivalAirport"
        val departureTime = Instant.now()
        val arrivalTime = Instant.now().plus(1, ChronoUnit.HOURS)
        val createdAt = Instant.now()

        val flightResult = mock<FlightResult>()
        whenever(flightResult.id).thenReturn(productId)
        whenever(flightResult.category).thenReturn(category)
        whenever(flightResult.name).thenReturn(name)
        whenever(flightResult.description).thenReturn(description)
        whenever(flightResult.price).thenReturn(price)
        whenever(flightResult.quantity).thenReturn(quantity)
        whenever(flightResult.airline).thenReturn(airline)
        whenever(flightResult.flightNumber).thenReturn(flightNumber)
        whenever(flightResult.departureAirport).thenReturn(departureAirport)
        whenever(flightResult.arrivalAirport).thenReturn(arrivalAirport)
        whenever(flightResult.departureTime).thenReturn(departureTime)
        whenever(flightResult.arrivalTime).thenReturn(arrivalTime)
        whenever(flightResult.createdAt).thenReturn(createdAt)

        whenever(
            productService.updateProduct(
                id = eq(productId),
                productUpdateCommand = any<FlightUpdateCommand>()
            )
        ).thenReturn(flightResult)

        mockMvc.perform(
            RestDocumentationRequestBuilders.patch("/products/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
						{
						  "category": "$category",
						  "name": "$name",
						  "description": "$description",
						  "price": $price,
						  "quantity": $quantity,
						  "airline": "$airline",
						  "flightNumber": "$flightNumber",
						  "departureAirport": "$departureAirport",
						  "arrivalAirport": "$arrivalAirport",
						  "departureTime": "$departureTime",
						  "arrivalTime": "$arrivalTime"
						}
						
						""".trimIndent()
                )
                .header("Authorization", "Bearer access-token")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "flight-update",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("category").description("카테고리"),
                        PayloadDocumentation.fieldWithPath("name").description("항공편명"),
                        PayloadDocumentation.fieldWithPath("description").description("설명"),
                        PayloadDocumentation.fieldWithPath("price").description("가격"),
                        PayloadDocumentation.fieldWithPath("quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("airline").description("항공사"),
                        PayloadDocumentation.fieldWithPath("flightNumber").description("항공편 ID"),
                        PayloadDocumentation.fieldWithPath("departureAirport").description("출발 공항"),
                        PayloadDocumentation.fieldWithPath("arrivalAirport").description("도착 공항"),
                        PayloadDocumentation.fieldWithPath("departureTime").description("출발 시간"),
                        PayloadDocumentation.fieldWithPath("arrivalTime").description("도착 시간")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("id").description("아이디"),
                        PayloadDocumentation.fieldWithPath("category").description("카테고리"),
                        PayloadDocumentation.fieldWithPath("name").description("항공편명"),
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
    fun updateAccommodation() {
        val productId = 1L
        val category = Category.ACCOMMODATION
        val name = "name"
        val description = "description"
        val price = 1000
        val quantity = 5
        val place = "place"
        val checkInTime = Instant.now()
        val checkOutTime = Instant.now().plus(1, ChronoUnit.DAYS)
        val createdAt = Instant.now()

        val accommodationResult = mock<AccommodationResult>()
        whenever(accommodationResult.id).thenReturn(productId)
        whenever(accommodationResult.category).thenReturn(category)
        whenever(accommodationResult.name).thenReturn(name)
        whenever(accommodationResult.description).thenReturn(description)
        whenever(accommodationResult.price).thenReturn(price)
        whenever(accommodationResult.quantity).thenReturn(quantity)
        whenever(accommodationResult.place).thenReturn(place)
        whenever(accommodationResult.checkInTime).thenReturn(checkInTime)
        whenever(accommodationResult.checkOutTime).thenReturn(checkOutTime)
        whenever(accommodationResult.createdAt).thenReturn(createdAt)

        whenever(
            productService.updateProduct(
                id = eq(productId),
                productUpdateCommand = any<AccommodationUpdateCommand>()
            )
        ).thenReturn(accommodationResult)

        mockMvc.perform(
            RestDocumentationRequestBuilders.patch("/products/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
						{
						  "category": "$category",
						  "name": "$name",
						  "description": "$description",
						  "price": $price,
						  "quantity": $quantity,
						  "place": "$place",
						  "checkInTime": "$checkInTime",
						  "checkOutTime": "$checkOutTime"
						}
						
						""".trimIndent()
                )
                .header("Authorization", "Bearer access-token")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "accommodation-update",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("category").description("카테고리"),
                        PayloadDocumentation.fieldWithPath("name").description("숙소명"),
                        PayloadDocumentation.fieldWithPath("description").description("설명"),
                        PayloadDocumentation.fieldWithPath("price").description("가격"),
                        PayloadDocumentation.fieldWithPath("quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("place").description("장소"),
                        PayloadDocumentation.fieldWithPath("checkInTime").description("체크인 시간"),
                        PayloadDocumentation.fieldWithPath("checkOutTime").description("체크아웃 시간")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("id").description("아이디"),
                        PayloadDocumentation.fieldWithPath("category").description("카테고리"),
                        PayloadDocumentation.fieldWithPath("name").description("숙소명"),
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

    @Test
    fun deleteTicket() {
        val productId = 1L
        val category = Category.TICKET

        doNothing().whenever(productService).deleteProduct(category = category, id = productId)

        mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/products/{id}", productId)
                .param("category", category.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer access-token")
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent())
            .andDo(
                MockMvcRestDocumentation.document(
                    "ticket-delete",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    )
                )
            )
    }

    @Test
    fun deleteFlight() {
        val productId = 1L
        val category = Category.FLIGHT

        doNothing().whenever(productService).deleteProduct(category = category, id = productId)

        mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/products/{id}", productId)
                .param("category", category.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer access-token")
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent())
            .andDo(
                MockMvcRestDocumentation.document(
                    "flight-delete",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    )
                )
            )
    }

    @Test
    fun deleteAccommodation() {
        val productId = 1L
        val category = Category.ACCOMMODATION

        doNothing().whenever(productService).deleteProduct(category = category, id = productId)

        mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/products/{id}", productId)
                .param("category", category.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer access-token")
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent())
            .andDo(
                MockMvcRestDocumentation.document(
                    "accommodation-delete",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    )
                )
            )
    }
}
