package spring.webmvc.presentation.controller.customer

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.justRun
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
import spring.webmvc.application.dto.result.ProductSummaryResult
import spring.webmvc.application.dto.result.WishlistResult
import spring.webmvc.application.service.ProductService
import spring.webmvc.application.service.WishlistService
import spring.webmvc.domain.dto.CursorPage
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.model.enums.ProductCategory
import spring.webmvc.domain.model.enums.ProductStatus
import spring.webmvc.domain.model.vo.ProductExposureAttribute
import spring.webmvc.infrastructure.config.ControllerTest
import java.time.Instant

@ControllerTest([MyController::class])
class MyControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var productService: ProductService

    @MockkBean
    private lateinit var wishlistService: WishlistService

    private lateinit var product1: Product
    private lateinit var product2: Product
    private lateinit var cursorPage: CursorPage<ProductSummaryResult>
    private lateinit var wishlistCursorPage: CursorPage<WishlistResult>

    @BeforeEach
    fun setUp() {
        val now = Instant.now()

        product1 = spyk(
            Product.create(
                category = ProductCategory.ACCOMMODATION,
                name = "제주도 호텔",
                description = "제주도 3박4일 패키지",
                price = 100000L,
                quantity = 10L,
                exposureAttribute = ProductExposureAttribute(
                    isPromotional = false,
                    isNewArrival = false,
                    isFeatured = true,
                    isLowStock = false
                )
            )
        ).apply {
            every { id } returns 1L
            every { status } returns ProductStatus.SELLING
            every { createdAt } returns now
        }

        product2 = spyk(
            Product.create(
                category = ProductCategory.TRANSPORT,
                name = "부산 KTX",
                description = "부산 왕복 KTX",
                price = 50000L,
                quantity = 100L,
                exposureAttribute = ProductExposureAttribute(
                    isPromotional = true,
                    isNewArrival = false,
                    isFeatured = false,
                    isLowStock = false
                )
            )
        ).apply {
            every { id } returns 2L
            every { status } returns ProductStatus.SELLING
            every { createdAt } returns now
        }

        cursorPage = CursorPage(
            content = listOf(
                ProductSummaryResult.of(product = product1, isRecentlyViewed = true),
                ProductSummaryResult.of(product = product2, isRecentlyViewed = true)
            ),
            size = 10L,
            hasNext = false,
            nextCursorId = null
        )

        wishlistCursorPage = CursorPage(
            content = listOf(
                WishlistResult(
                    id = 1L,
                    product = ProductSummaryResult.of(product = product1, isWished = true),
                    createdAt = now
                ),
                WishlistResult(
                    id = 2L,
                    product = ProductSummaryResult.of(product = product2, isWished = true),
                    createdAt = now
                )
            ),
            size = 10L,
            hasNext = false,
            nextCursorId = null
        )
    }

    @Test
    fun findRecentlyViewedProducts() {
        every { productService.findRecentlyViewedProducts(any(), any()) } returns cursorPage

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/customer/my/recently-viewed")
                .header("Authorization", "Bearer access-token")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "customer-my-recently-viewed",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    RequestDocumentation.queryParameters(
                        RequestDocumentation.parameterWithName("cursorId").description("커서 ID").optional()
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("size").description("페이지 크기"),
                        PayloadDocumentation.fieldWithPath("hasNext").description("다음 페이지 존재 여부"),
                        PayloadDocumentation.fieldWithPath("nextCursorId").description("다음 페이지 커서 ID"),
                        PayloadDocumentation.fieldWithPath("content[].id").description("상품 ID"),
                        PayloadDocumentation.fieldWithPath("content[].category").description("카테고리"),
                        PayloadDocumentation.fieldWithPath("content[].status").description("상태"),
                        PayloadDocumentation.fieldWithPath("content[].name").description("상품명"),
                        PayloadDocumentation.fieldWithPath("content[].description").description("설명"),
                        PayloadDocumentation.fieldWithPath("content[].price").description("가격"),
                        PayloadDocumentation.fieldWithPath("content[].quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("content[].exposureAttribute").description("노출 속성"),
                        PayloadDocumentation.fieldWithPath("content[].exposureAttribute.isPromotional")
                            .description("프로모션 여부"),
                        PayloadDocumentation.fieldWithPath("content[].exposureAttribute.isNewArrival")
                            .description("신상품 여부"),
                        PayloadDocumentation.fieldWithPath("content[].exposureAttribute.isFeatured")
                            .description("추천 상품 여부"),
                        PayloadDocumentation.fieldWithPath("content[].exposureAttribute.isLowStock")
                            .description("품절 임박 여부"),
                        PayloadDocumentation.fieldWithPath("content[].exposureAttribute.isRecommended")
                            .description("추천 여부"),
                        PayloadDocumentation.fieldWithPath("content[].exposureAttribute.isPersonalPick")
                            .description("개인 추천 여부"),
                        PayloadDocumentation.fieldWithPath("content[].exposureAttribute.isRecentlyViewed")
                            .description("최근 본 상품 여부"),
                        PayloadDocumentation.fieldWithPath("content[].exposureAttribute.isWished")
                            .description("찜 여부"),
                        PayloadDocumentation.fieldWithPath("content[].createdAt").description("생성일시")
                    )
                )
            )
    }

    @Test
    fun findWishlists() {
        every { wishlistService.findWishlists(any(), any()) } returns wishlistCursorPage

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/customer/my/wishlists")
                .header("Authorization", "Bearer access-token")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "customer-my-wishlists-list",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    RequestDocumentation.queryParameters(
                        RequestDocumentation.parameterWithName("cursorId").description("커서 ID").optional()
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("size").description("페이지 크기"),
                        PayloadDocumentation.fieldWithPath("hasNext").description("다음 페이지 존재 여부"),
                        PayloadDocumentation.fieldWithPath("nextCursorId").description("다음 페이지 커서 ID"),
                        PayloadDocumentation.fieldWithPath("content[].id").description("찜 ID"),
                        PayloadDocumentation.fieldWithPath("content[].createdAt").description("찜 등록일시"),
                        PayloadDocumentation.fieldWithPath("content[].product.id").description("상품 ID"),
                        PayloadDocumentation.fieldWithPath("content[].product.category").description("카테고리"),
                        PayloadDocumentation.fieldWithPath("content[].product.status").description("상태"),
                        PayloadDocumentation.fieldWithPath("content[].product.name").description("상품명"),
                        PayloadDocumentation.fieldWithPath("content[].product.description").description("설명"),
                        PayloadDocumentation.fieldWithPath("content[].product.price").description("가격"),
                        PayloadDocumentation.fieldWithPath("content[].product.quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("content[].product.exposureAttribute").description("노출 속성"),
                        PayloadDocumentation.fieldWithPath("content[].product.exposureAttribute.isPromotional")
                            .description("프로모션 여부"),
                        PayloadDocumentation.fieldWithPath("content[].product.exposureAttribute.isNewArrival")
                            .description("신상품 여부"),
                        PayloadDocumentation.fieldWithPath("content[].product.exposureAttribute.isFeatured")
                            .description("추천 상품 여부"),
                        PayloadDocumentation.fieldWithPath("content[].product.exposureAttribute.isLowStock")
                            .description("품절 임박 여부"),
                        PayloadDocumentation.fieldWithPath("content[].product.exposureAttribute.isRecommended")
                            .description("추천 여부"),
                        PayloadDocumentation.fieldWithPath("content[].product.exposureAttribute.isPersonalPick")
                            .description("개인 추천 여부"),
                        PayloadDocumentation.fieldWithPath("content[].product.exposureAttribute.isRecentlyViewed")
                            .description("최근 본 상품 여부"),
                        PayloadDocumentation.fieldWithPath("content[].product.exposureAttribute.isWished")
                            .description("찜 여부"),
                        PayloadDocumentation.fieldWithPath("content[].product.createdAt").description("상품 생성일시")
                    )
                )
            )
    }

    @Test
    fun addWishlist() {
        justRun { wishlistService.addWishlist(any(), any()) }

        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/customer/my/wishlists")
                .header("Authorization", "Bearer access-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"productId": 1}""")
        )
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andDo(
                MockMvcRestDocumentation.document(
                    "customer-my-wishlists-add",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("productId").description("상품 ID")
                    )
                )
            )
    }

    @Test
    fun removeWishlist() {
        justRun { wishlistService.removeWishlist(any(), any()) }

        mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/customer/my/wishlists/{wishlistId}", 1L)
                .header("Authorization", "Bearer access-token")
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent())
            .andDo(
                MockMvcRestDocumentation.document(
                    "customer-my-wishlists-remove",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("wishlistId").description("찜 ID")
                    )
                )
            )
    }
}
