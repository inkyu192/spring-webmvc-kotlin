package spring.webmvc.presentation.controller

import io.mockk.every
import io.mockk.spyk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.*
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
import spring.webmvc.application.dto.result.TicketResult
import spring.webmvc.application.service.ProductService
import spring.webmvc.domain.model.cache.AccommodationCache
import spring.webmvc.domain.model.cache.FlightCache
import spring.webmvc.domain.model.cache.TicketCache
import spring.webmvc.domain.model.entity.Accommodation
import spring.webmvc.domain.model.entity.Flight
import spring.webmvc.domain.model.entity.Ticket
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.infrastructure.config.WebMvcTestConfig
import spring.webmvc.infrastructure.persistence.dto.CursorPage
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
        val nextCursorId: Long? = null
        val size = 10
        val name = "name"
        val response = listOf(
            spyk(
                Accommodation.create(
                    name = "name1",
                    description = "description",
                    price = 1000,
                    quantity = 10,
                    place = "place1",
                    checkInTime = Instant.now(),
                    checkOutTime = Instant.now().plusSeconds(3600)
                )
            ).apply { every { id } returns 1L },
            spyk(
                Flight.create(
                    name = "name2",
                    description = "description",
                    price = 2000,
                    quantity = 20,
                    airline = "airline",
                    flightNumber = "FL123",
                    departureAirport = "ICN",
                    arrivalAirport = "NRT",
                    departureTime = Instant.now(),
                    arrivalTime = Instant.now().plusSeconds(7200)
                )
            ).apply { every { id } returns 2L },
            spyk(
                Ticket.create(
                    name = "name3",
                    description = "description",
                    price = 3000,
                    quantity = 30,
                    place = "place3",
                    performanceTime = Instant.now(),
                    duration = "2h",
                    ageLimit = "All"
                )
            ).apply { every { id } returns 3L },
        )
        val cursorPage = CursorPage(
            content = response,
            size = size,
            hasNext = false,
            nextCursorId = null
        )

        whenever(productService.findProducts(cursorId = nextCursorId, size = size, name = name)).thenReturn(cursorPage)

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/products")
                .header("Authorization", "Bearer access-token")
                .queryParam("nextCursorId", null)
                .queryParam("size", size.toString())
                .queryParam("name", name)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "product-list",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    RequestDocumentation.queryParameters(
                        RequestDocumentation.parameterWithName("nextCursorId").description("다음 페이지 커서 ID").optional(),
                        RequestDocumentation.parameterWithName("size").description("페이지 크기").optional(),
                        RequestDocumentation.parameterWithName("name").description("상품명").optional()
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("products[].id").description("아이디"),
                        PayloadDocumentation.fieldWithPath("products[].category").description("카테고리"),
                        PayloadDocumentation.fieldWithPath("products[].name").description("상품명"),
                        PayloadDocumentation.fieldWithPath("products[].description").description("설명"),
                        PayloadDocumentation.fieldWithPath("products[].price").description("가격"),
                        PayloadDocumentation.fieldWithPath("products[].quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("products[].createdAt").description("생성일시"),

                        PayloadDocumentation.fieldWithPath("page.size").description("페이지 크기"),
                        PayloadDocumentation.fieldWithPath("page.hasNext").description("다음 페이지 존재 여부"),
                        PayloadDocumentation.fieldWithPath("page.nextCursorId").description("다음 페이지 커서 ID")
                    )
                )
            )
    }

    @Test
    fun findTicket() {
        val productId = 1L
        val category = Category.TICKET
        val ticketResult = TicketResult(
            TicketCache(
                id = productId,
                name = "name",
                description = "description",
                price = 1000,
                quantity = 10,
                createdAt = Instant.now(),
                ticketId = 1L,
                place = "place",
                performanceTime = Instant.now(),
                duration = "duration",
                ageLimit = "ageLimit"
            )
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
            FlightCache(
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
            AccommodationCache(
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
        val price = 1000L
        val quantity = 5L
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
        val price = 1000L
        val quantity = 5L
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
        val price = 1000L
        val quantity = 5L
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
        val price = 1000L
        val quantity = 5L
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
        val price = 1000L
        val quantity = 5L
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
        val price = 1000L
        val quantity = 5L
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
                .queryParam("category", category.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer access-token")
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent())
            .andDo(
                MockMvcRestDocumentation.document(
                    "ticket-delete",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    RequestDocumentation.queryParameters(
                        RequestDocumentation.parameterWithName("category").description("카테고리")
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
                .queryParam("category", category.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer access-token")
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent())
            .andDo(
                MockMvcRestDocumentation.document(
                    "flight-delete",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    RequestDocumentation.queryParameters(
                        RequestDocumentation.parameterWithName("category").description("카테고리")
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
                .queryParam("category", category.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer access-token")
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent())
            .andDo(
                MockMvcRestDocumentation.document(
                    "accommodation-delete",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    RequestDocumentation.queryParameters(
                        RequestDocumentation.parameterWithName("category").description("카테고리")
                    )
                )
            )
    }
}
