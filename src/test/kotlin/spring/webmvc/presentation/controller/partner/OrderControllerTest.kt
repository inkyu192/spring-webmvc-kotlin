package spring.webmvc.presentation.controller.partner

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import spring.webmvc.application.dto.query.OrderOffsetPageQuery
import spring.webmvc.application.dto.result.OrderDetailResult
import spring.webmvc.application.dto.result.OrderProductResult
import spring.webmvc.application.dto.result.OrderSummaryResult
import spring.webmvc.application.service.OrderService
import spring.webmvc.domain.model.enums.OrderStatus
import spring.webmvc.infrastructure.config.ControllerTest
import java.time.Instant

@ControllerTest([OrderController::class])
class OrderControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var orderService: OrderService
    private lateinit var orderDetailResult: OrderDetailResult
    private lateinit var orderProductResult: OrderProductResult
    private lateinit var pageable: Pageable
    private lateinit var page: PageImpl<OrderSummaryResult>
    private val userId = 1L
    private val orderId = 1L
    private val orderStatus = OrderStatus.ORDER

    @BeforeEach
    fun setUp() {
        orderProductResult = OrderProductResult(
            name = "name",
            price = 5000,
            quantity = 3
        )

        orderDetailResult = OrderDetailResult(
            id = orderId,
            orderedAt = Instant.now(),
            status = OrderStatus.ORDER,
            products = listOf(orderProductResult)
        )

        val orderSummaryResult = OrderSummaryResult(
            id = orderId,
            orderedAt = Instant.now(),
            status = OrderStatus.ORDER
        )

        pageable = PageRequest.of(0, 10)
        page = PageImpl(listOf(orderSummaryResult), pageable, 1)
    }

    @Test
    fun findOrders() {
        every {
            orderService.findOrdersWithOffsetPage(
                query = any<OrderOffsetPageQuery>()
            )
        } returns page

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/partner/orders")
                .header("Authorization", "Bearer access-token")
                .queryParam("page", pageable.pageNumber.toString())
                .queryParam("size", pageable.pageSize.toString())
                .queryParam("userId", userId.toString())
                .queryParam("orderStatus", orderStatus.toString())
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "partner-order-list",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    RequestDocumentation.queryParameters(
                        RequestDocumentation.parameterWithName("page").description("페이지 번호").optional(),
                        RequestDocumentation.parameterWithName("size").description("페이지 크기").optional(),
                        RequestDocumentation.parameterWithName("userId").description("사용자 아이디").optional(),
                        RequestDocumentation.parameterWithName("orderStatus").description("주문상태").optional()
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("page").description("현재 페이지 번호"),
                        PayloadDocumentation.fieldWithPath("size").description("페이지 크기"),
                        PayloadDocumentation.fieldWithPath("totalElements").description("전체 아이템 수"),
                        PayloadDocumentation.fieldWithPath("totalPages").description("전체 페이지 수"),
                        PayloadDocumentation.fieldWithPath("hasNext").description("다음 페이지 존재 여부"),
                        PayloadDocumentation.fieldWithPath("hasPrevious").description("이전 페이지 존재 여부"),
                        PayloadDocumentation.fieldWithPath("content[].id").description("주문아이디"),
                        PayloadDocumentation.fieldWithPath("content[].orderedAt").description("주문일시"),
                        PayloadDocumentation.fieldWithPath("content[].status").description("주문상태")
                    )
                )
            )
    }

    @Test
    fun findOrder() {
        every { orderService.findOrder(id = orderId) } returns orderDetailResult

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/partner/orders/{id}", orderId)
                .header("Authorization", "Bearer access-token")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "partner-order-detail",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("id").description("아이디")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("id").description("주문아이디"),
                        PayloadDocumentation.fieldWithPath("orderedAt").description("주문일시"),
                        PayloadDocumentation.fieldWithPath("status").description("주문상태"),
                        PayloadDocumentation.fieldWithPath("products[].name").description("상품명"),
                        PayloadDocumentation.fieldWithPath("products[].price").description("주문가격"),
                        PayloadDocumentation.fieldWithPath("products[].quantity").description("주문수량")
                    )
                )
            )
    }

    @Test
    fun updateOrderStatus() {
        val updatedOrderDetailResult = OrderDetailResult(
            id = orderId,
            orderedAt = Instant.now(),
            status = OrderStatus.CONFIRM,
            products = listOf(orderProductResult)
        )

        every { orderService.updateOrderStatus(command = any()) } returns updatedOrderDetailResult

        mockMvc.perform(
            RestDocumentationRequestBuilders.patch("/partner/orders/{id}", orderId)
                .header("Authorization", "Bearer access-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                          "orderStatus": "CONFIRM"
                        }
                    """.trimIndent()
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "partner-order-update-status",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("id").description("아이디")
                    ),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("orderStatus").description("주문상태")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("id").description("주문아이디"),
                        PayloadDocumentation.fieldWithPath("orderedAt").description("주문일시"),
                        PayloadDocumentation.fieldWithPath("status").description("주문상태"),
                        PayloadDocumentation.fieldWithPath("products[].name").description("상품명"),
                        PayloadDocumentation.fieldWithPath("products[].price").description("주문가격"),
                        PayloadDocumentation.fieldWithPath("products[].quantity").description("주문수량")
                    )
                )
            )
    }
}
