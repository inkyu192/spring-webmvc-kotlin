package spring.webmvc.presentation.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import spring.webmvc.application.dto.command.AccommodationCreateCommand
import spring.webmvc.application.dto.command.AccommodationUpdateCommand
import spring.webmvc.application.dto.command.TransportCreateCommand
import spring.webmvc.application.dto.command.TransportUpdateCommand
import spring.webmvc.application.dto.result.AccommodationResult
import spring.webmvc.application.dto.result.TransportResult
import spring.webmvc.application.service.ProductService
import spring.webmvc.domain.model.cache.AccommodationCache
import spring.webmvc.domain.model.cache.TransportCache
import spring.webmvc.domain.model.entity.Accommodation
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.model.entity.Transport
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.infrastructure.config.ControllerTest
import spring.webmvc.infrastructure.persistence.dto.CursorPage
import java.time.Instant
import java.time.temporal.ChronoUnit

@ControllerTest([ProductController::class])
class ProductControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var productService: ProductService
    private lateinit var accommodation: Accommodation
    private lateinit var transport: Transport
    private lateinit var cursorPage: CursorPage<Product>
    private lateinit var transportResult: TransportResult
    private lateinit var accommodationResult: AccommodationResult
    private lateinit var transportCache: TransportCache
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
        ).apply {
            every { id } returns 1L
            every { product.id } returns 1L
            every { product.category } returns Category.ACCOMMODATION
            every { product.name } returns "name1"
            every { product.description } returns "description"
            every { product.price } returns 1000
            every { product.quantity } returns 10
            every { product.createdAt } returns now
        }

        transport = spyk(
            Transport.create(
                name = "name2",
                description = "description",
                price = 2000,
                quantity = 20,
                departureLocation = "Seoul",
                arrivalLocation = "Busan",
                departureTime = now,
                arrivalTime = now.plusSeconds(7200)
            )
        ).apply {
            every { id } returns 2L
            every { product.id } returns 2L
            every { product.category } returns Category.TRANSPORT
            every { product.name } returns "name2"
            every { product.description } returns "description"
            every { product.price } returns 2000
            every { product.quantity } returns 20
            every { product.createdAt } returns now
        }

        cursorPage = CursorPage(
            content = listOf(accommodation.product, transport.product),
            size = size,
            hasNext = false,
            nextCursorId = null
        )

        transportCache = TransportCache(
            id = productId,
            name = name,
            description = description,
            price = price,
            quantity = quantity,
            createdAt = now,
            transportId = 1L,
            departureLocation = "Seoul",
            arrivalLocation = "Busan",
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

        transportResult = TransportResult(transportCache)
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
    fun findTransport() {
        val category = Category.TRANSPORT

        every { productService.findProduct(id = productId, category = category) } returns transportResult

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/products/{id}", productId)
                .header("Authorization", "Bearer access-token")
                .queryParam("category", category.toString())
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "transport-detail",
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
                        PayloadDocumentation.fieldWithPath("name").description("교통수단명"),
                        PayloadDocumentation.fieldWithPath("description").description("설명"),
                        PayloadDocumentation.fieldWithPath("price").description("가격"),
                        PayloadDocumentation.fieldWithPath("quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("createdAt").description("생성일시"),
                        PayloadDocumentation.fieldWithPath("departureLocation").description("출발지"),
                        PayloadDocumentation.fieldWithPath("arrivalLocation").description("도착지"),
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
                    "accommodation-detail",
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
    fun createTransport() {
        val productId = 1L
        val category = Category.TRANSPORT
        val name = "name"
        val description = "description"
        val price = 1000L
        val quantity = 5L
        val departureLocation = "Seoul"
        val arrivalLocation = "Busan"
        val departureTime = Instant.now()
        val arrivalTime = Instant.now().plus(1, ChronoUnit.HOURS)
        val createdAt = Instant.now()

        val transportResult = mockk<TransportResult>(relaxed = true)
        every { transportResult.id } returns productId
        every { transportResult.category } returns category
        every { transportResult.name } returns name
        every { transportResult.description } returns description
        every { transportResult.price } returns price
        every { transportResult.quantity } returns quantity
        every { transportResult.transportId } returns productId
        every { transportResult.departureLocation } returns departureLocation
        every { transportResult.arrivalLocation } returns arrivalLocation
        every { transportResult.departureTime } returns departureTime
        every { transportResult.arrivalTime } returns arrivalTime
        every { transportResult.createdAt } returns createdAt

        every { productService.createProduct(any<TransportCreateCommand>()) } returns transportResult

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
						  "departureLocation": "$departureLocation",
						  "arrivalLocation": "$arrivalLocation",
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
                    "transport-create",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("category").description("카테고리"),
                        PayloadDocumentation.fieldWithPath("name").description("교통수단명"),
                        PayloadDocumentation.fieldWithPath("description").description("설명"),
                        PayloadDocumentation.fieldWithPath("price").description("가격"),
                        PayloadDocumentation.fieldWithPath("quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("departureLocation").description("출발지"),
                        PayloadDocumentation.fieldWithPath("arrivalLocation").description("도착지"),
                        PayloadDocumentation.fieldWithPath("departureTime").description("출발 시간"),
                        PayloadDocumentation.fieldWithPath("arrivalTime").description("도착 시간")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("id").description("아이디"),
                        PayloadDocumentation.fieldWithPath("category").description("카테고리"),
                        PayloadDocumentation.fieldWithPath("name").description("교통수단명"),
                        PayloadDocumentation.fieldWithPath("description").description("설명"),
                        PayloadDocumentation.fieldWithPath("price").description("가격"),
                        PayloadDocumentation.fieldWithPath("quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("createdAt").description("생성일시"),
                        PayloadDocumentation.fieldWithPath("departureLocation").description("출발지"),
                        PayloadDocumentation.fieldWithPath("arrivalLocation").description("도착지"),
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

        val accommodationResult = mockk<AccommodationResult>(relaxed = true)
        every { accommodationResult.id } returns productId
        every { accommodationResult.category } returns category
        every { accommodationResult.name } returns name
        every { accommodationResult.description } returns description
        every { accommodationResult.price } returns price
        every { accommodationResult.quantity } returns quantity
        every { accommodationResult.accommodationId } returns productId
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
    fun updateTransport() {
        val productId = 1L
        val category = Category.TRANSPORT
        val name = "name"
        val description = "description"
        val price = 1000L
        val quantity = 5L
        val departureLocation = "Seoul"
        val arrivalLocation = "Busan"
        val departureTime = Instant.now()
        val arrivalTime = Instant.now().plus(1, ChronoUnit.HOURS)
        val createdAt = Instant.now()

        val transportResult = mockk<TransportResult>(relaxed = true)
        every { transportResult.id } returns productId
        every { transportResult.category } returns category
        every { transportResult.name } returns name
        every { transportResult.description } returns description
        every { transportResult.price } returns price
        every { transportResult.quantity } returns quantity
        every { transportResult.transportId } returns productId
        every { transportResult.departureLocation } returns departureLocation
        every { transportResult.arrivalLocation } returns arrivalLocation
        every { transportResult.departureTime } returns departureTime
        every { transportResult.arrivalTime } returns arrivalTime
        every { transportResult.createdAt } returns createdAt

        every {
            productService.updateProduct(
                id = productId,
                productUpdateCommand = any<TransportUpdateCommand>()
            )
        } returns transportResult

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
						  "departureLocation": "$departureLocation",
						  "arrivalLocation": "$arrivalLocation",
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
                    "transport-update",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("category").description("카테고리"),
                        PayloadDocumentation.fieldWithPath("name").description("교통수단명"),
                        PayloadDocumentation.fieldWithPath("description").description("설명"),
                        PayloadDocumentation.fieldWithPath("price").description("가격"),
                        PayloadDocumentation.fieldWithPath("quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("departureLocation").description("출발지"),
                        PayloadDocumentation.fieldWithPath("arrivalLocation").description("도착지"),
                        PayloadDocumentation.fieldWithPath("departureTime").description("출발 시간"),
                        PayloadDocumentation.fieldWithPath("arrivalTime").description("도착 시간")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("id").description("아이디"),
                        PayloadDocumentation.fieldWithPath("category").description("카테고리"),
                        PayloadDocumentation.fieldWithPath("name").description("교통수단명"),
                        PayloadDocumentation.fieldWithPath("description").description("설명"),
                        PayloadDocumentation.fieldWithPath("price").description("가격"),
                        PayloadDocumentation.fieldWithPath("quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("createdAt").description("생성일시"),
                        PayloadDocumentation.fieldWithPath("departureLocation").description("출발지"),
                        PayloadDocumentation.fieldWithPath("arrivalLocation").description("도착지"),
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

        val accommodationResult = mockk<AccommodationResult>(relaxed = true)
        every { accommodationResult.id } returns productId
        every { accommodationResult.category } returns category
        every { accommodationResult.name } returns name
        every { accommodationResult.description } returns description
        every { accommodationResult.price } returns price
        every { accommodationResult.quantity } returns quantity
        every { accommodationResult.accommodationId } returns productId
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
    fun deleteTransport() {
        val productId = 1L
        val category = Category.TRANSPORT

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
                    "transport-delete",
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
