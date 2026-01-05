package spring.webmvc.presentation.controller.customer

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.spyk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import spring.webmvc.application.dto.query.ProductCursorPageQuery
import spring.webmvc.application.dto.result.*
import spring.webmvc.application.service.ProductService
import spring.webmvc.domain.model.entity.Accommodation
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.model.entity.Transport
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.domain.model.enums.ProductStatus
import spring.webmvc.infrastructure.config.ControllerTest
import spring.webmvc.infrastructure.persistence.dto.CursorPage
import java.time.Instant

@ControllerTest([CustomerProductController::class])
class CustomerProductControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var productService: ProductService
    private lateinit var accommodation: Accommodation
    private lateinit var transport: Transport
    private lateinit var cursorPage: CursorPage<ProductSummaryResult>
    private lateinit var transportResult: ProductDetailResult
    private lateinit var accommodationResult: ProductDetailResult
    private val productId = 1L
    private val name = "name"
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
            every { product.status } returns ProductStatus.SELLING
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
            every { product.status } returns ProductStatus.SELLING
            every { product.name } returns "name2"
            every { product.description } returns "description"
            every { product.price } returns 2000
            every { product.quantity } returns 20
            every { product.createdAt } returns now
        }

        cursorPage = CursorPage(
            content = listOf(
                ProductSummaryResult.from(accommodation.product),
                ProductSummaryResult.from(transport.product)
            ),
            size = size.toLong(),
            hasNext = false,
            nextCursorId = null
        )

        transportResult = ProductDetailResult.from(
            product = transport.product,
            attributeResult = TransportResult.from(transport)
        )
        accommodationResult = ProductDetailResult.from(
            product = accommodation.product,
            attributeResult = AccommodationResult.from(accommodation)
        )
    }

    @Test
    fun findProducts() {
        every {
            productService.findProductsWithCursorPage(
                query = ProductCursorPageQuery(
                    cursorId = nextCursorId,
                    name = name,
                    status = ProductStatus.SELLING,
                )
            )
        } returns cursorPage

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/customer/products")
                .header("Authorization", "Bearer access-token")
                .queryParam("cursorId", null)
                .queryParam("name", name)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "customer-product-list",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    RequestDocumentation.queryParameters(
                        RequestDocumentation.parameterWithName("cursorId").description("커서 ID").optional(),
                        RequestDocumentation.parameterWithName("name").description("상품명").optional()
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("products[].id").description("아이디"),
                        PayloadDocumentation.fieldWithPath("products[].category").description("카테고리"),
                        PayloadDocumentation.fieldWithPath("products[].status").description("상태"),
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
        every { productService.findProduct(id = productId) } returns transportResult

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/customer/products/{id}", productId)
                .header("Authorization", "Bearer access-token")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "customer-transport-detail",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("id").description("아이디")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("id").description("아이디"),
                        PayloadDocumentation.fieldWithPath("category").description("카테고리"),
                        PayloadDocumentation.fieldWithPath("status").description("상태"),
                        PayloadDocumentation.fieldWithPath("name").description("교통수단명"),
                        PayloadDocumentation.fieldWithPath("description").description("설명"),
                        PayloadDocumentation.fieldWithPath("price").description("가격"),
                        PayloadDocumentation.fieldWithPath("quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("createdAt").description("생성일시"),
                        PayloadDocumentation.fieldWithPath("attribute").description("상세 정보"),
                        PayloadDocumentation.fieldWithPath("attribute.departureLocation").description("출발지"),
                        PayloadDocumentation.fieldWithPath("attribute.arrivalLocation").description("도착지"),
                        PayloadDocumentation.fieldWithPath("attribute.departureTime").description("출발 시간"),
                        PayloadDocumentation.fieldWithPath("attribute.arrivalTime").description("도착 시간")
                    )
                )
            )
    }

    @Test
    fun findAccommodation() {
        val productId = 1L

        every { productService.findProduct(id = productId) } returns accommodationResult

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/customer/products/{id}", productId)
                .header("Authorization", "Bearer access-token")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "customer-accommodation-detail",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("id").description("아이디")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("id").description("아이디"),
                        PayloadDocumentation.fieldWithPath("category").description("카테고리"),
                        PayloadDocumentation.fieldWithPath("status").description("상태"),
                        PayloadDocumentation.fieldWithPath("name").description("티켓명"),
                        PayloadDocumentation.fieldWithPath("description").description("설명"),
                        PayloadDocumentation.fieldWithPath("price").description("가격"),
                        PayloadDocumentation.fieldWithPath("quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("createdAt").description("생성일시"),
                        PayloadDocumentation.fieldWithPath("attribute").description("상세 정보"),
                        PayloadDocumentation.fieldWithPath("attribute.accommodationId").description("숙소아이디"),
                        PayloadDocumentation.fieldWithPath("attribute.place").description("장소"),
                        PayloadDocumentation.fieldWithPath("attribute.checkInTime").description("체크인 시간"),
                        PayloadDocumentation.fieldWithPath("attribute.checkOutTime").description("체크아웃 시간")
                    )
                )
            )
    }
}
