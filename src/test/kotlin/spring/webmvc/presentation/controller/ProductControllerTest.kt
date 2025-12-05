package spring.webmvc.presentation.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
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
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.model.entity.Ticket
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.infrastructure.config.WebMvcTestConfig
import spring.webmvc.infrastructure.persistence.dto.CursorPage
import spring.webmvc.presentation.controller.support.MockMvcRestDocsSetup
import java.time.Instant
import java.time.temporal.ChronoUnit

@WebMvcTest(ProductController::class)
@Import(WebMvcTestConfig::class)
class ProductControllerTest() : MockMvcRestDocsSetup() {
    @MockkBean
    private lateinit var productService: ProductService
    private lateinit var accommodation: Accommodation
    private lateinit var flight: Flight
    private lateinit var ticket: Ticket
    private lateinit var cursorPage: CursorPage<Product>
    private lateinit var ticketResult: TicketResult
    private lateinit var flightResult: FlightResult
    private lateinit var accommodationResult: AccommodationResult
    private lateinit var ticketCache: TicketCache
    private lateinit var flightCache: FlightCache
    private lateinit var accommodationCache: AccommodationCache
    private val productId = 1L
    private val name = "name"
    private val description = "description"
    private val price = 1000L
    private val quantity = 5L
    private val size = 10
    private val nextCursorId: Long? = null

    @BeforeEach
    fun setUp() {
        val now = Instant.now()

        accommodation = spyk(
            Accommodation.create(
                name = "name1",
                description = "description",
                price = 1000,
                quantity = 10,
                place = "place1",
                checkInTime = now,
                checkOutTime = now.plusSeconds(3600)
            )
        ).apply { every { id } returns 1L }

        flight = spyk(
            Flight.create(
                name = "name2",
                description = "description",
                price = 2000,
                quantity = 20,
                airline = "airline",
                flightNumber = "FL123",
                departureAirport = "ICN",
                arrivalAirport = "NRT",
                departureTime = now,
                arrivalTime = now.plusSeconds(7200)
            )
        ).apply { every { id } returns 2L }

        ticket = spyk(
            Ticket.create(
                name = "name3",
                description = "description",
                price = 3000,
                quantity = 30,
                place = "place3",
                performanceTime = now,
                duration = "2h",
                ageLimit = "All"
            )
        ).apply { every { id } returns 3L }

        cursorPage = CursorPage(
            content = listOf(accommodation, flight, ticket),
            size = size,
            hasNext = false,
            nextCursorId = null
        )

        ticketCache = TicketCache(
            id = productId,
            name = name,
            description = description,
            price = price,
            quantity = quantity,
            createdAt = now,
            ticketId = 1L,
            place = "place",
            performanceTime = now,
            duration = "duration",
            ageLimit = "ageLimit"
        )

        flightCache = FlightCache(
            id = productId,
            name = name,
            description = description,
            price = price,
            quantity = quantity,
            createdAt = now,
            flightId = 1L,
            airline = "airline",
            flightNumber = "flightNumber",
            departureAirport = "departureAirport",
            arrivalAirport = "arrivalAirport",
            departureTime = now,
            arrivalTime = now.plus(1, ChronoUnit.DAYS)
        )

        accommodationCache = AccommodationCache(
            id = productId,
            name = name,
            description = description,
            price = price,
            quantity = quantity,
            createdAt = now,
            accommodationId = 1L,
            place = "place",
            checkInTime = now,
            checkOutTime = now.plus(1, ChronoUnit.DAYS)
        )

        ticketResult = TicketResult(ticketCache)
        flightResult = FlightResult(flightCache)
        accommodationResult = AccommodationResult(accommodationCache)
    }

    @Test
    fun findProducts() {
        every { productService.findProducts(cursorId = nextCursorId, size = size, name = name) } returns cursorPage

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
        val category = Category.TICKET

        every { productService.findProduct(id = productId, category = category) } returns ticketResult

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
        val category = Category.FLIGHT

        every { productService.findProduct(id = productId, category = category) } returns flightResult

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

        every { productService.findProduct(id = productId, category = category) } returns accommodationResult

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

        val ticketResult = mockk<TicketResult>()
        every { ticketResult.id } returns productId
        every { ticketResult.category } returns category
        every { ticketResult.name } returns name
        every { ticketResult.description } returns description
        every { ticketResult.price } returns price
        every { ticketResult.quantity } returns quantity
        every { ticketResult.place } returns place
        every { ticketResult.performanceTime } returns performanceTime
        every { ticketResult.duration } returns duration
        every { ticketResult.ageLimit } returns ageLimit
        every { ticketResult.createdAt } returns createdAt

        every { productService.createProduct(any<TicketCreateCommand>()) } returns ticketResult

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

        val flightResult = mockk<FlightResult>()
        every { flightResult.id } returns productId
        every { flightResult.category } returns category
        every { flightResult.name } returns name
        every { flightResult.description } returns description
        every { flightResult.price } returns price
        every { flightResult.quantity } returns quantity
        every { flightResult.airline } returns airline
        every { flightResult.flightNumber } returns flightNumber
        every { flightResult.departureAirport } returns departureAirport
        every { flightResult.arrivalAirport } returns arrivalAirport
        every { flightResult.departureTime } returns departureTime
        every { flightResult.arrivalTime } returns arrivalTime
        every { flightResult.createdAt } returns createdAt

        every { productService.createProduct(any<FlightCreateCommand>()) } returns flightResult

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

        val accommodationResult = mockk<AccommodationResult>()
        every { accommodationResult.id } returns productId
        every { accommodationResult.category } returns category
        every { accommodationResult.name } returns name
        every { accommodationResult.description } returns description
        every { accommodationResult.price } returns price
        every { accommodationResult.quantity } returns quantity
        every { accommodationResult.place } returns place
        every { accommodationResult.checkInTime } returns checkInTime
        every { accommodationResult.checkOutTime } returns checkOutTime
        every { accommodationResult.createdAt } returns createdAt

        every { productService.createProduct(any<AccommodationCreateCommand>()) } returns accommodationResult

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

        val ticketResult = mockk<TicketResult>()
        every { ticketResult.id } returns productId
        every { ticketResult.category } returns category
        every { ticketResult.name } returns name
        every { ticketResult.description } returns description
        every { ticketResult.price } returns price
        every { ticketResult.quantity } returns quantity
        every { ticketResult.place } returns place
        every { ticketResult.performanceTime } returns performanceTime
        every { ticketResult.duration } returns duration
        every { ticketResult.ageLimit } returns ageLimit
        every { ticketResult.createdAt } returns createdAt

        every {
            productService.updateProduct(
                id = productId,
                productUpdateCommand = any<TicketUpdateCommand>()
            )
        } returns ticketResult

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
                        PayloadDocumentation.fieldWithPath("performanceTime").description(  "공연 시간"),
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

        val flightResult = mockk<FlightResult>()
        every { flightResult.id } returns productId
        every { flightResult.category } returns category
        every { flightResult.name } returns name
        every { flightResult.description } returns description
        every { flightResult.price } returns price
        every { flightResult.quantity } returns quantity
        every { flightResult.airline } returns airline
        every { flightResult.flightNumber } returns flightNumber
        every { flightResult.departureAirport } returns departureAirport
        every { flightResult.arrivalAirport } returns arrivalAirport
        every { flightResult.departureTime } returns departureTime
        every { flightResult.arrivalTime } returns arrivalTime
        every { flightResult.createdAt } returns createdAt

        every {
            productService.updateProduct(
                id = productId,
                productUpdateCommand = any<FlightUpdateCommand>()
            )
        } returns flightResult

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

        val accommodationResult = mockk<AccommodationResult>()
        every { accommodationResult.id } returns productId
        every { accommodationResult.category } returns category
        every { accommodationResult.name } returns name
        every { accommodationResult.description } returns description
        every { accommodationResult.price } returns price
        every { accommodationResult.quantity } returns quantity
        every { accommodationResult.place } returns place
        every { accommodationResult.checkInTime } returns checkInTime
        every { accommodationResult.checkOutTime } returns checkOutTime
        every { accommodationResult.createdAt } returns createdAt

        every {
            productService.updateProduct(
                id = productId,
                productUpdateCommand = any<AccommodationUpdateCommand>()
            )
        } returns accommodationResult

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

        every { productService.deleteProduct(category = category, id = productId) } just runs

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

        every { productService.deleteProduct(category = category, id = productId) } just runs

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

        every { productService.deleteProduct(category = category, id = productId) } just runs

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
