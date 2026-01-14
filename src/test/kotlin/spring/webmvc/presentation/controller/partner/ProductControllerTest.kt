package spring.webmvc.presentation.controller.partner

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.just
import io.mockk.runs
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
import spring.webmvc.application.dto.command.ProductPutCommand
import spring.webmvc.application.dto.query.ProductOffsetPageQuery
import spring.webmvc.application.dto.result.AccommodationResult
import spring.webmvc.application.dto.result.ProductDetailResult
import spring.webmvc.application.dto.result.ProductSummaryResult
import spring.webmvc.application.dto.result.TransportResult
import spring.webmvc.application.service.ProductService
import spring.webmvc.domain.model.entity.Accommodation
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.model.entity.Transport
import spring.webmvc.domain.model.enums.ProductCategory
import spring.webmvc.domain.model.enums.ProductStatus
import spring.webmvc.domain.model.vo.ProductExposureProperty
import spring.webmvc.infrastructure.config.ControllerTest
import java.time.Instant

@ControllerTest([ProductController::class])
class ProductControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var productService: ProductService
    private lateinit var accommodation: Accommodation
    private lateinit var transport: Transport
    private lateinit var transportResult: ProductDetailResult
    private lateinit var accommodationResult: ProductDetailResult
    private val productId = 1L
    private val name = "name"

    @BeforeEach
    fun setUp() {
        val now = Instant.now()

        val accommodationProduct = Product.create(
            category = ProductCategory.ACCOMMODATION,
            name = "name1",
            description = "description",
            price = 1000,
            quantity = 10,
            exposureProperty = ProductExposureProperty(
                isPromotional = false,
                isNewArrival = false,
                isFeatured = false,
                isLowStock = false
            )
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
            every { product.category } returns ProductCategory.ACCOMMODATION
            every { product.status } returns ProductStatus.SELLING
            every { product.name } returns "name1"
            every { product.description } returns "description"
            every { product.price } returns 1000
            every { product.quantity } returns 10
            every { product.exposureProperty } returns ProductExposureProperty(
                isPromotional = false,
                isNewArrival = false,
                isFeatured = true,
                isLowStock = false
            )
            every { product.createdAt } returns now
        }

        val transportProduct = Product.create(
            category = ProductCategory.TRANSPORT,
            name = "name2",
            description = "description",
            price = 2000,
            quantity = 20,
            exposureProperty = ProductExposureProperty(
                isPromotional = false,
                isNewArrival = false,
                isFeatured = false,
                isLowStock = false
            )
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
            every { product.category } returns ProductCategory.TRANSPORT
            every { product.status } returns ProductStatus.SELLING
            every { product.name } returns "name2"
            every { product.description } returns "description"
            every { product.price } returns 2000
            every { product.quantity } returns 20
            every { product.exposureProperty } returns ProductExposureProperty(
                isPromotional = true,
                isNewArrival = false,
                isFeatured = false,
                isLowStock = false
            )
            every { product.createdAt } returns now
        }

        transportResult = ProductDetailResult.from(
            product = transport.product,
            propertyResult = TransportResult.from(transport)
        )
        accommodationResult = ProductDetailResult.from(
            product = accommodation.product,
            propertyResult = AccommodationResult.from(accommodation)
        )
    }

    @Test
    fun findProducts() {
        val pageable = PageRequest.of(0, 10)
        val page = PageImpl(
            listOf(
                ProductSummaryResult.from(accommodation.product),
                ProductSummaryResult.from(transport.product)
            ),
            pageable,
            2
        )

        every {
            productService.findProductsWithOffsetPage(
                query = ProductOffsetPageQuery(
                    pageable = pageable,
                    name = name,
                    status = null,
                )
            )
        } returns page

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/partner/products")
                .header("Authorization", "Bearer access-token")
                .queryParam("page", "0")
                .queryParam("size", "10")
                .queryParam("name", name)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "partner-product-list",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    RequestDocumentation.queryParameters(
                        RequestDocumentation.parameterWithName("page").description("페이지 번호").optional(),
                        RequestDocumentation.parameterWithName("size").description("페이지 크기").optional(),
                        RequestDocumentation.parameterWithName("name").description("상품명")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("page").description("현재 페이지 번호"),
                        PayloadDocumentation.fieldWithPath("size").description("페이지 크기"),
                        PayloadDocumentation.fieldWithPath("totalElements").description("전체 요소 수"),
                        PayloadDocumentation.fieldWithPath("totalPages").description("전체 페이지 수"),
                        PayloadDocumentation.fieldWithPath("hasNext").description("다음 페이지 존재 여부"),
                        PayloadDocumentation.fieldWithPath("hasPrevious").description("이전 페이지 존재 여부"),
                        PayloadDocumentation.fieldWithPath("content[].id").description("아이디"),
                        PayloadDocumentation.fieldWithPath("content[].category").description("카테고리"),
                        PayloadDocumentation.fieldWithPath("content[].status").description("상태"),
                        PayloadDocumentation.fieldWithPath("content[].name").description("상품명"),
                        PayloadDocumentation.fieldWithPath("content[].description").description("설명"),
                        PayloadDocumentation.fieldWithPath("content[].price").description("가격"),
                        PayloadDocumentation.fieldWithPath("content[].quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("content[].exposureProperty").description("노출 속성")
                            .optional(),
                        PayloadDocumentation.fieldWithPath("content[].exposureProperty.isPromotional")
                            .description("프로모션 여부").optional(),
                        PayloadDocumentation.fieldWithPath("content[].exposureProperty.isNewArrival")
                            .description("신상품 여부").optional(),
                        PayloadDocumentation.fieldWithPath("content[].exposureProperty.isFeatured")
                            .description("추천 상품 여부").optional(),
                        PayloadDocumentation.fieldWithPath("content[].exposureProperty.isLowStock")
                            .description("품절 임박 여부").optional(),
                        PayloadDocumentation.fieldWithPath("content[].createdAt").description("생성일시")
                    )
                )
            )
    }

    @Test
    fun findTransport() {
        every { productService.findProduct(id = productId) } returns transportResult

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/partner/products/{id}", productId)
                .header("Authorization", "Bearer access-token")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "partner-transport-detail",
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
                        PayloadDocumentation.fieldWithPath("exposureProperty").description("노출 속성").optional(),
                        PayloadDocumentation.fieldWithPath("exposureProperty.isPromotional").description("프로모션 여부")
                            .optional(),
                        PayloadDocumentation.fieldWithPath("exposureProperty.isNewArrival").description("신상품 여부")
                            .optional(),
                        PayloadDocumentation.fieldWithPath("exposureProperty.isFeatured").description("추천 상품 여부")
                            .optional(),
                        PayloadDocumentation.fieldWithPath("exposureProperty.isLowStock").description("품절 임박 여부")
                            .optional(),
                        PayloadDocumentation.fieldWithPath("createdAt").description("생성일시"),
                        PayloadDocumentation.fieldWithPath("property").description("상세 정보"),
                        PayloadDocumentation.fieldWithPath("property.departureLocation").description("출발지"),
                        PayloadDocumentation.fieldWithPath("property.arrivalLocation").description("도착지"),
                        PayloadDocumentation.fieldWithPath("property.departureTime").description("출발 시간"),
                        PayloadDocumentation.fieldWithPath("property.arrivalTime").description("도착 시간")
                    )
                )
            )
    }

    @Test
    fun findAccommodation() {
        val productId = 1L

        every { productService.findProduct(id = productId) } returns accommodationResult

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/partner/products/{id}", productId)
                .header("Authorization", "Bearer access-token")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "partner-accommodation-detail",
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
                        PayloadDocumentation.fieldWithPath("exposureProperty").description("노출 속성").optional(),
                        PayloadDocumentation.fieldWithPath("exposureProperty.isPromotional").description("프로모션 여부")
                            .optional(),
                        PayloadDocumentation.fieldWithPath("exposureProperty.isNewArrival").description("신상품 여부")
                            .optional(),
                        PayloadDocumentation.fieldWithPath("exposureProperty.isFeatured").description("추천 상품 여부")
                            .optional(),
                        PayloadDocumentation.fieldWithPath("exposureProperty.isLowStock").description("품절 임박 여부")
                            .optional(),
                        PayloadDocumentation.fieldWithPath("createdAt").description("생성일시"),
                        PayloadDocumentation.fieldWithPath("property").description("상세 정보"),
                        PayloadDocumentation.fieldWithPath("property.place").description("장소"),
                        PayloadDocumentation.fieldWithPath("property.checkInTime").description("체크인 시간"),
                        PayloadDocumentation.fieldWithPath("property.checkOutTime").description("체크아웃 시간")
                    )
                )
            )
    }

    @Test
    fun createTransport() {
        val category = ProductCategory.TRANSPORT
        val status = ProductStatus.SELLING
        val name = "name"
        val description = "description"
        val price = 1000L
        val quantity = 5L
        val departureLocation = "Seoul"
        val arrivalLocation = "Busan"
        val departureTime = Instant.now()
        val arrivalTime = Instant.now().plusSeconds(3600)

        every { productService.createProduct(any<ProductPutCommand>()) } returns transportResult

        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/partner/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
					{
					  "status": "$status",
					  "category": "$category",
					  "name": "$name",
					  "description": "$description",
					  "price": $price,
					  "quantity": $quantity,
					  "exposureProperty": {
					    "isPromotional": false,
					    "isNewArrival": false,
					    "isFeatured": false,
					    "isLowStock": false
					  },
					  "property": {
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
                    "partner-transport-create",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("status").description("상태"),
                        PayloadDocumentation.fieldWithPath("category").description("카테고리"),
                        PayloadDocumentation.fieldWithPath("name").description("교통수단명"),
                        PayloadDocumentation.fieldWithPath("description").description("설명"),
                        PayloadDocumentation.fieldWithPath("price").description("가격"),
                        PayloadDocumentation.fieldWithPath("quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("exposureProperty").description("노출 속성"),
                        PayloadDocumentation.fieldWithPath("exposureProperty.isPromotional").description("프로모션 여부"),
                        PayloadDocumentation.fieldWithPath("exposureProperty.isNewArrival").description("신상품 여부"),
                        PayloadDocumentation.fieldWithPath("exposureProperty.isFeatured").description("추천 상품 여부"),
                        PayloadDocumentation.fieldWithPath("exposureProperty.isLowStock").description("품절 임박 여부"),
                        PayloadDocumentation.fieldWithPath("property").description("상세 정보"),
                        PayloadDocumentation.fieldWithPath("property.departureLocation").description("출발지"),
                        PayloadDocumentation.fieldWithPath("property.arrivalLocation").description("도착지"),
                        PayloadDocumentation.fieldWithPath("property.departureTime").description("출발 시간"),
                        PayloadDocumentation.fieldWithPath("property.arrivalTime").description("도착 시간")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("id").description("아이디"),
                        PayloadDocumentation.fieldWithPath("category").description("카테고리"),
                        PayloadDocumentation.fieldWithPath("status").description("상태"),
                        PayloadDocumentation.fieldWithPath("name").description("교통수단명"),
                        PayloadDocumentation.fieldWithPath("description").description("설명"),
                        PayloadDocumentation.fieldWithPath("price").description("가격"),
                        PayloadDocumentation.fieldWithPath("quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("exposureProperty").description("노출 속성"),
                        PayloadDocumentation.fieldWithPath("exposureProperty.isPromotional").description("프로모션 여부"),
                        PayloadDocumentation.fieldWithPath("exposureProperty.isNewArrival").description("신상품 여부"),
                        PayloadDocumentation.fieldWithPath("exposureProperty.isFeatured").description("추천 상품 여부"),
                        PayloadDocumentation.fieldWithPath("exposureProperty.isLowStock").description("품절 임박 여부"),
                        PayloadDocumentation.fieldWithPath("createdAt").description("생성일시"),
                        PayloadDocumentation.fieldWithPath("property").description("상세 정보"),
                        PayloadDocumentation.fieldWithPath("property.departureLocation").description("출발지"),
                        PayloadDocumentation.fieldWithPath("property.arrivalLocation").description("도착지"),
                        PayloadDocumentation.fieldWithPath("property.departureTime").description("출발 시간"),
                        PayloadDocumentation.fieldWithPath("property.arrivalTime").description("도착 시간")
                    )
                )
            )
    }

    @Test
    fun createAccommodation() {
        val category = ProductCategory.ACCOMMODATION
        val status = ProductStatus.SELLING
        val name = "name"
        val description = "description"
        val price = 1000L
        val quantity = 5L
        val place = "place"
        val checkInTime = Instant.now()
        val checkOutTime = Instant.now().plusSeconds(86400)

        every { productService.createProduct(any<ProductPutCommand>()) } returns accommodationResult

        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/partner/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
					{
					  "status": "$status",
					  "category": "$category",
					  "name": "$name",
					  "description": "$description",
					  "price": $price,
					  "quantity": $quantity,
					  "exposureProperty": {
					    "isPromotional": false,
					    "isNewArrival": false,
					    "isFeatured": false,
					    "isLowStock": false
					  },
					  "property": {
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
                    "partner-accommodation-create",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("status").description("상태"),
                        PayloadDocumentation.fieldWithPath("category").description("카테고리"),
                        PayloadDocumentation.fieldWithPath("name").description("숙소명"),
                        PayloadDocumentation.fieldWithPath("description").description("설명"),
                        PayloadDocumentation.fieldWithPath("price").description("가격"),
                        PayloadDocumentation.fieldWithPath("quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("exposureProperty").description("노출 속성"),
                        PayloadDocumentation.fieldWithPath("exposureProperty.isPromotional").description("프로모션 여부"),
                        PayloadDocumentation.fieldWithPath("exposureProperty.isNewArrival").description("신상품 여부"),
                        PayloadDocumentation.fieldWithPath("exposureProperty.isFeatured").description("추천 상품 여부"),
                        PayloadDocumentation.fieldWithPath("exposureProperty.isLowStock").description("품절 임박 여부"),
                        PayloadDocumentation.fieldWithPath("property").description("상세 정보"),
                        PayloadDocumentation.fieldWithPath("property.place").description("장소"),
                        PayloadDocumentation.fieldWithPath("property.checkInTime").description("체크인 시간"),
                        PayloadDocumentation.fieldWithPath("property.checkOutTime").description("체크아웃 시간")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("id").description("아이디"),
                        PayloadDocumentation.fieldWithPath("category").description("카테고리"),
                        PayloadDocumentation.fieldWithPath("status").description("상태"),
                        PayloadDocumentation.fieldWithPath("name").description("숙소명"),
                        PayloadDocumentation.fieldWithPath("description").description("설명"),
                        PayloadDocumentation.fieldWithPath("price").description("가격"),
                        PayloadDocumentation.fieldWithPath("quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("exposureProperty").description("노출 속성"),
                        PayloadDocumentation.fieldWithPath("exposureProperty.isPromotional").description("프로모션 여부"),
                        PayloadDocumentation.fieldWithPath("exposureProperty.isNewArrival").description("신상품 여부"),
                        PayloadDocumentation.fieldWithPath("exposureProperty.isFeatured").description("추천 상품 여부"),
                        PayloadDocumentation.fieldWithPath("exposureProperty.isLowStock").description("품절 임박 여부"),
                        PayloadDocumentation.fieldWithPath("createdAt").description("생성일시"),
                        PayloadDocumentation.fieldWithPath("property").description("상세 정보"),
                        PayloadDocumentation.fieldWithPath("property.place").description("장소"),
                        PayloadDocumentation.fieldWithPath("property.checkInTime").description("체크인 시간"),
                        PayloadDocumentation.fieldWithPath("property.checkOutTime").description("체크아웃 시간")
                    )
                )
            )
    }

    @Test
    fun replaceTransport() {
        val productId = 1L
        val category = ProductCategory.TRANSPORT
        val status = ProductStatus.SELLING
        val name = "name"
        val description = "description"
        val price = 1000L
        val quantity = 5L
        val departureLocation = "Seoul"
        val arrivalLocation = "Busan"
        val departureTime = Instant.now()
        val arrivalTime = Instant.now().plusSeconds(3600)

        every {
            productService.replaceProduct(
                command = any<ProductPutCommand>()
            )
        } returns transportResult

        mockMvc.perform(
            RestDocumentationRequestBuilders.put("/partner/products/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
					{
					  "status": "$status",
					  "category": "$category",
					  "name": "$name",
					  "description": "$description",
					  "price": $price,
					  "quantity": $quantity,
					  "exposureProperty": {
					    "isPromotional": false,
					    "isNewArrival": false,
					    "isFeatured": false,
					    "isLowStock": false
					  },
					  "property": {
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
                    "partner-transport-replace",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("status").description("상태"),
                        PayloadDocumentation.fieldWithPath("category").description("카테고리"),
                        PayloadDocumentation.fieldWithPath("name").description("교통수단명"),
                        PayloadDocumentation.fieldWithPath("description").description("설명"),
                        PayloadDocumentation.fieldWithPath("price").description("가격"),
                        PayloadDocumentation.fieldWithPath("quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("exposureProperty").description("노출 속성"),
                        PayloadDocumentation.fieldWithPath("exposureProperty.isPromotional").description("프로모션 여부"),
                        PayloadDocumentation.fieldWithPath("exposureProperty.isNewArrival").description("신상품 여부"),
                        PayloadDocumentation.fieldWithPath("exposureProperty.isFeatured").description("추천 상품 여부"),
                        PayloadDocumentation.fieldWithPath("exposureProperty.isLowStock").description("품절 임박 여부"),
                        PayloadDocumentation.fieldWithPath("property").description("상세 정보"),
                        PayloadDocumentation.fieldWithPath("property.departureLocation").description("출발지"),
                        PayloadDocumentation.fieldWithPath("property.arrivalLocation").description("도착지"),
                        PayloadDocumentation.fieldWithPath("property.departureTime").description("출발 시간"),
                        PayloadDocumentation.fieldWithPath("property.arrivalTime").description("도착 시간")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("id").description("아이디"),
                        PayloadDocumentation.fieldWithPath("category").description("카테고리"),
                        PayloadDocumentation.fieldWithPath("status").description("상태"),
                        PayloadDocumentation.fieldWithPath("name").description("교통수단명"),
                        PayloadDocumentation.fieldWithPath("description").description("설명"),
                        PayloadDocumentation.fieldWithPath("price").description("가격"),
                        PayloadDocumentation.fieldWithPath("quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("exposureProperty").description("노출 속성"),
                        PayloadDocumentation.fieldWithPath("exposureProperty.isPromotional").description("프로모션 여부"),
                        PayloadDocumentation.fieldWithPath("exposureProperty.isNewArrival").description("신상품 여부"),
                        PayloadDocumentation.fieldWithPath("exposureProperty.isFeatured").description("추천 상품 여부"),
                        PayloadDocumentation.fieldWithPath("exposureProperty.isLowStock").description("품절 임박 여부"),
                        PayloadDocumentation.fieldWithPath("createdAt").description("생성일시"),
                        PayloadDocumentation.fieldWithPath("property").description("상세 정보"),
                        PayloadDocumentation.fieldWithPath("property.departureLocation").description("출발지"),
                        PayloadDocumentation.fieldWithPath("property.arrivalLocation").description("도착지"),
                        PayloadDocumentation.fieldWithPath("property.departureTime").description("출발 시간"),
                        PayloadDocumentation.fieldWithPath("property.arrivalTime").description("도착 시간")
                    )
                )
            )
    }

    @Test
    fun replaceAccommodation() {
        val productId = 1L
        val category = ProductCategory.ACCOMMODATION
        val status = ProductStatus.SELLING
        val name = "name"
        val description = "description"
        val price = 1000L
        val quantity = 5L
        val place = "place"
        val checkInTime = Instant.now()
        val checkOutTime = Instant.now().plusSeconds(86400)

        every {
            productService.replaceProduct(
                command = any<ProductPutCommand>()
            )
        } returns accommodationResult

        mockMvc.perform(
            RestDocumentationRequestBuilders.put("/partner/products/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
					{
					  "status": "$status",
					  "category": "$category",
					  "name": "$name",
					  "description": "$description",
					  "price": $price,
					  "quantity": $quantity,
					  "exposureProperty": {
					    "isPromotional": false,
					    "isNewArrival": false,
					    "isFeatured": false,
					    "isLowStock": false
					  },
					  "property": {
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
                    "partner-accommodation-replace",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("status").description("상태"),
                        PayloadDocumentation.fieldWithPath("category").description("카테고리"),
                        PayloadDocumentation.fieldWithPath("name").description("숙소명"),
                        PayloadDocumentation.fieldWithPath("description").description("설명"),
                        PayloadDocumentation.fieldWithPath("price").description("가격"),
                        PayloadDocumentation.fieldWithPath("quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("exposureProperty").description("노출 속성"),
                        PayloadDocumentation.fieldWithPath("exposureProperty.isPromotional").description("프로모션 여부"),
                        PayloadDocumentation.fieldWithPath("exposureProperty.isNewArrival").description("신상품 여부"),
                        PayloadDocumentation.fieldWithPath("exposureProperty.isFeatured").description("추천 상품 여부"),
                        PayloadDocumentation.fieldWithPath("exposureProperty.isLowStock").description("품절 임박 여부"),
                        PayloadDocumentation.fieldWithPath("property").description("상세 정보"),
                        PayloadDocumentation.fieldWithPath("property.place").description("장소"),
                        PayloadDocumentation.fieldWithPath("property.checkInTime").description("체크인 시간"),
                        PayloadDocumentation.fieldWithPath("property.checkOutTime").description("체크아웃 시간")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("id").description("아이디"),
                        PayloadDocumentation.fieldWithPath("category").description("카테고리"),
                        PayloadDocumentation.fieldWithPath("status").description("상태"),
                        PayloadDocumentation.fieldWithPath("name").description("숙소명"),
                        PayloadDocumentation.fieldWithPath("description").description("설명"),
                        PayloadDocumentation.fieldWithPath("price").description("가격"),
                        PayloadDocumentation.fieldWithPath("quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("exposureProperty").description("노출 속성"),
                        PayloadDocumentation.fieldWithPath("exposureProperty.isPromotional").description("프로모션 여부"),
                        PayloadDocumentation.fieldWithPath("exposureProperty.isNewArrival").description("신상품 여부"),
                        PayloadDocumentation.fieldWithPath("exposureProperty.isFeatured").description("추천 상품 여부"),
                        PayloadDocumentation.fieldWithPath("exposureProperty.isLowStock").description("품절 임박 여부"),
                        PayloadDocumentation.fieldWithPath("createdAt").description("생성일시"),
                        PayloadDocumentation.fieldWithPath("property").description("상세 정보"),
                        PayloadDocumentation.fieldWithPath("property.place").description("장소"),
                        PayloadDocumentation.fieldWithPath("property.checkInTime").description("체크인 시간"),
                        PayloadDocumentation.fieldWithPath("property.checkOutTime").description("체크아웃 시간")
                    )
                )
            )
    }

    @Test
    fun deleteTransport() {
        val productId = 1L

        every { productService.deleteProduct(productId) } just runs

        mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/partner/products/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer access-token")
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent())
            .andDo(
                MockMvcRestDocumentation.document(
                    "partner-transport-delete",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("id").description("아이디")
                    )
                )
            )
    }

    @Test
    fun deleteAccommodation() {
        val productId = 1L

        every { productService.deleteProduct(productId) } just runs

        mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/partner/products/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer access-token")
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent())
            .andDo(
                MockMvcRestDocumentation.document(
                    "partner-accommodation-delete",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("id").description("아이디")
                    )
                )
            )
    }
}
