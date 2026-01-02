package spring.webmvc.presentation.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.spyk
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
import spring.webmvc.application.dto.command.ProductDeleteCommand
import spring.webmvc.application.dto.command.ProductPutCommand
import spring.webmvc.application.dto.result.ProductResult
import spring.webmvc.application.service.ProductService
import spring.webmvc.domain.model.entity.Accommodation
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.model.entity.Transport
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.infrastructure.config.ControllerTest
import spring.webmvc.infrastructure.persistence.dto.CursorPage
import java.time.Instant

@ControllerTest([ProductController::class])
class ProductControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var productService: ProductService
    private lateinit var accommodation: Accommodation
    private lateinit var transport: Transport
    private lateinit var cursorPage: CursorPage<Product>
    private lateinit var transportResult: ProductResult
    private lateinit var accommodationResult: ProductResult
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

        val accommodationProduct = Product.create(
            category = Category.ACCOMMODATION,
            name = "name1",
            description = "description",
            price = 1000,
            quantity = 10
        )
        accommodation = spyk(
            Accommodation.create(
                product = accommodationProduct,
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

        val transportProduct = Product.create(
            category = Category.TRANSPORT,
            name = "name2",
            description = "description",
            price = 2000,
            quantity = 20
        )
        transport = spyk(
            Transport.create(
                product = transportProduct,
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

        transportResult = ProductResult.from(transport)
        accommodationResult = ProductResult.from(accommodation)
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
                        PayloadDocumentation.fieldWithPath("detail").description("상세 정보"),
                        PayloadDocumentation.fieldWithPath("detail.departureLocation").description("출발지"),
                        PayloadDocumentation.fieldWithPath("detail.arrivalLocation").description("도착지"),
                        PayloadDocumentation.fieldWithPath("detail.departureTime").description("출발 시간"),
                        PayloadDocumentation.fieldWithPath("detail.arrivalTime").description("도착 시간")
                    )
                )
            )
    }

    @Test
    fun findAccommodation() {
        val productId = 1L
        val category = Category.ACCOMMODATION
        val accommodationResult = ProductResult.from(accommodation)

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
                        PayloadDocumentation.fieldWithPath("detail").description("상세 정보"),
                        PayloadDocumentation.fieldWithPath("detail.accommodationId").description("숙소아이디"),
                        PayloadDocumentation.fieldWithPath("detail.place").description("장소"),
                        PayloadDocumentation.fieldWithPath("detail.checkInTime").description("체크인 시간"),
                        PayloadDocumentation.fieldWithPath("detail.checkOutTime").description("체크아웃 시간")
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
        val arrivalTime = Instant.now().plusSeconds(3600)
        val createdAt = Instant.now()

        every { productService.createProduct(any<ProductPutCommand>()) } returns transportResult

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
						  "detail": {
						    "type": "TRANSPORT",
						    "departureLocation": "$departureLocation",
						    "arrivalLocation": "$arrivalLocation",
						    "departureTime": "$departureTime",
						    "arrivalTime": "$arrivalTime"
						  }
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
                        PayloadDocumentation.fieldWithPath("detail").description("상세 정보"),
                        PayloadDocumentation.fieldWithPath("detail.type").description("상품 타입"),
                        PayloadDocumentation.fieldWithPath("detail.departureLocation").description("출발지"),
                        PayloadDocumentation.fieldWithPath("detail.arrivalLocation").description("도착지"),
                        PayloadDocumentation.fieldWithPath("detail.departureTime").description("출발 시간"),
                        PayloadDocumentation.fieldWithPath("detail.arrivalTime").description("도착 시간")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("id").description("아이디"),
                        PayloadDocumentation.fieldWithPath("category").description("카테고리"),
                        PayloadDocumentation.fieldWithPath("name").description("교통수단명"),
                        PayloadDocumentation.fieldWithPath("description").description("설명"),
                        PayloadDocumentation.fieldWithPath("price").description("가격"),
                        PayloadDocumentation.fieldWithPath("quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("createdAt").description("생성일시"),
                        PayloadDocumentation.fieldWithPath("detail").description("상세 정보"),
                        PayloadDocumentation.fieldWithPath("detail.departureLocation").description("출발지"),
                        PayloadDocumentation.fieldWithPath("detail.arrivalLocation").description("도착지"),
                        PayloadDocumentation.fieldWithPath("detail.departureTime").description("출발 시간"),
                        PayloadDocumentation.fieldWithPath("detail.arrivalTime").description("도착 시간")
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
        val checkOutTime = Instant.now().plusSeconds(86400)
        val createdAt = Instant.now()

        every { productService.createProduct(any<ProductPutCommand>()) } returns accommodationResult

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
						  "detail": {
						    "type": "ACCOMMODATION",
						    "place": "$place",
						    "checkInTime": "$checkInTime",
						    "checkOutTime": "$checkOutTime"
						  }
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
                        PayloadDocumentation.fieldWithPath("detail").description("상세 정보"),
                        PayloadDocumentation.fieldWithPath("detail.type").description("상품 타입"),
                        PayloadDocumentation.fieldWithPath("detail.place").description("장소"),
                        PayloadDocumentation.fieldWithPath("detail.checkInTime").description("체크인 시간"),
                        PayloadDocumentation.fieldWithPath("detail.checkOutTime").description("체크아웃 시간")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("id").description("아이디"),
                        PayloadDocumentation.fieldWithPath("category").description("카테고리"),
                        PayloadDocumentation.fieldWithPath("name").description("숙소명"),
                        PayloadDocumentation.fieldWithPath("description").description("설명"),
                        PayloadDocumentation.fieldWithPath("price").description("가격"),
                        PayloadDocumentation.fieldWithPath("quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("createdAt").description("생성일시"),
                        PayloadDocumentation.fieldWithPath("detail").description("상세 정보"),
                        PayloadDocumentation.fieldWithPath("detail.accommodationId").description("숙소아이디"),
                        PayloadDocumentation.fieldWithPath("detail.place").description("장소"),
                        PayloadDocumentation.fieldWithPath("detail.checkInTime").description("체크인 시간"),
                        PayloadDocumentation.fieldWithPath("detail.checkOutTime").description("체크아웃 시간")
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
        val arrivalTime = Instant.now().plusSeconds(3600)
        val createdAt = Instant.now()

        every {
            productService.updateProduct(
                command = any<ProductPutCommand>()
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
						  "detail": {
						    "type": "TRANSPORT",
						    "departureLocation": "$departureLocation",
						    "arrivalLocation": "$arrivalLocation",
						    "departureTime": "$departureTime",
						    "arrivalTime": "$arrivalTime"
						  }
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
                        PayloadDocumentation.fieldWithPath("detail").description("상세 정보"),
                        PayloadDocumentation.fieldWithPath("detail.type").description("상품 타입"),
                        PayloadDocumentation.fieldWithPath("detail.departureLocation").description("출발지"),
                        PayloadDocumentation.fieldWithPath("detail.arrivalLocation").description("도착지"),
                        PayloadDocumentation.fieldWithPath("detail.departureTime").description("출발 시간"),
                        PayloadDocumentation.fieldWithPath("detail.arrivalTime").description("도착 시간")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("id").description("아이디"),
                        PayloadDocumentation.fieldWithPath("category").description("카테고리"),
                        PayloadDocumentation.fieldWithPath("name").description("교통수단명"),
                        PayloadDocumentation.fieldWithPath("description").description("설명"),
                        PayloadDocumentation.fieldWithPath("price").description("가격"),
                        PayloadDocumentation.fieldWithPath("quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("createdAt").description("생성일시"),
                        PayloadDocumentation.fieldWithPath("detail").description("상세 정보"),
                        PayloadDocumentation.fieldWithPath("detail.departureLocation").description("출발지"),
                        PayloadDocumentation.fieldWithPath("detail.arrivalLocation").description("도착지"),
                        PayloadDocumentation.fieldWithPath("detail.departureTime").description("출발 시간"),
                        PayloadDocumentation.fieldWithPath("detail.arrivalTime").description("도착 시간")
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
        val checkOutTime = Instant.now().plusSeconds(86400)
        val createdAt = Instant.now()

        every {
            productService.updateProduct(
                command = any<ProductPutCommand>()
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
						  "detail": {
						    "type": "ACCOMMODATION",
						    "place": "$place",
						    "checkInTime": "$checkInTime",
						    "checkOutTime": "$checkOutTime"
						  }
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
                        PayloadDocumentation.fieldWithPath("detail").description("상세 정보"),
                        PayloadDocumentation.fieldWithPath("detail.type").description("상품 타입"),
                        PayloadDocumentation.fieldWithPath("detail.place").description("장소"),
                        PayloadDocumentation.fieldWithPath("detail.checkInTime").description("체크인 시간"),
                        PayloadDocumentation.fieldWithPath("detail.checkOutTime").description("체크아웃 시간")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("id").description("아이디"),
                        PayloadDocumentation.fieldWithPath("category").description("카테고리"),
                        PayloadDocumentation.fieldWithPath("name").description("숙소명"),
                        PayloadDocumentation.fieldWithPath("description").description("설명"),
                        PayloadDocumentation.fieldWithPath("price").description("가격"),
                        PayloadDocumentation.fieldWithPath("quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("createdAt").description("생성일시"),
                        PayloadDocumentation.fieldWithPath("detail").description("상세 정보"),
                        PayloadDocumentation.fieldWithPath("detail.accommodationId").description("숙소아이디"),
                        PayloadDocumentation.fieldWithPath("detail.place").description("장소"),
                        PayloadDocumentation.fieldWithPath("detail.checkInTime").description("체크인 시간"),
                        PayloadDocumentation.fieldWithPath("detail.checkOutTime").description("체크아웃 시간")
                    )
                )
            )
    }

    @Test
    fun deleteTransport() {
        val productId = 1L
        val category = Category.TRANSPORT

        every { productService.deleteProduct(any<ProductDeleteCommand>()) } just runs

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

        every { productService.deleteProduct(any<ProductDeleteCommand>()) } just runs

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
