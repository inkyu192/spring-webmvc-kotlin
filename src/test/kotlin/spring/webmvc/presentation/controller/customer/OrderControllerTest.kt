package spring.webmvc.presentation.controller.customer

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockkObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
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
import spring.webmvc.application.dto.command.OrderCreateCommand
import spring.webmvc.application.dto.command.OrderProductCreateCommand
import spring.webmvc.application.dto.query.OrderCursorPageQuery
import spring.webmvc.application.dto.result.OrderDetailResult
import spring.webmvc.application.dto.result.OrderProductResult
import spring.webmvc.application.dto.result.OrderSummaryResult
import spring.webmvc.application.service.OrderService
import spring.webmvc.domain.model.enums.OrderStatus
import spring.webmvc.infrastructure.config.ControllerTest
import spring.webmvc.infrastructure.persistence.dto.CursorPage
import spring.webmvc.infrastructure.security.SecurityContextUtil
import java.time.Instant

@ControllerTest([OrderController::class])
class OrderControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var orderService: OrderService
    private lateinit var orderDetailResult: OrderDetailResult
    private lateinit var orderProductResult: OrderProductResult
    private lateinit var orderProductCreateCommand: OrderProductCreateCommand
    private lateinit var orderCreateCommand: OrderCreateCommand
    private lateinit var pageable: Pageable
    private lateinit var cursorPage: CursorPage<OrderSummaryResult>
    private val productId = 1L
    private val quantity = 3L
    private val userId = 1L
    private val orderId = 1L
    private val orderStatus = OrderStatus.ORDER

    @BeforeEach
    fun setUp() {
        mockkObject(SecurityContextUtil)
        every { SecurityContextUtil.getUserId() } returns userId

        orderProductCreateCommand = OrderProductCreateCommand(id = productId, quantity = quantity)
        orderCreateCommand = OrderCreateCommand(userId = userId, products = listOf(orderProductCreateCommand))

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
        cursorPage = CursorPage(
            content = listOf(orderSummaryResult),
            size = 10,
            hasNext = false,
            nextCursorId = null
        )
    }

    @Test
    fun createOrder() {
        every { orderService.createOrder(command = orderCreateCommand) } returns orderDetailResult

        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/customer/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer access-token")
                .content(
                    """
                        {
                          "products": [
                            {
                              "id": $productId,
                              "quantity": $quantity
                            }
                          ]
                        }
                    """.trimIndent()
                )
        )
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andDo(
                MockMvcRestDocumentation.document(
                    "customer-order-create",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("products[].id").description("상품아이디"),
                        PayloadDocumentation.fieldWithPath("products[].quantity").description("주문수량")
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
    fun findOrders() {
        every {
            orderService.findOrdersWithCursorPage(
                query = any<OrderCursorPageQuery>()
            )
        } returns cursorPage

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/customer/orders")
                .header("Authorization", "Bearer access-token")
                .queryParam("cursorId", null)
                .queryParam("orderStatus", orderStatus.toString())
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "customer-order-list",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    RequestDocumentation.queryParameters(
                        RequestDocumentation.parameterWithName("cursorId").description("커서 ID").optional(),
                        RequestDocumentation.parameterWithName("orderStatus").description("주문상태").optional()
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("orders[].id").description("주문아이디"),
                        PayloadDocumentation.fieldWithPath("orders[].orderedAt").description("주문일시"),
                        PayloadDocumentation.fieldWithPath("orders[].status").description("주문상태"),
                        PayloadDocumentation.fieldWithPath("page.size").description("페이지 크기"),
                        PayloadDocumentation.fieldWithPath("page.hasNext").description("다음 페이지 존재 여부"),
                        PayloadDocumentation.fieldWithPath("page.nextCursorId").description("다음 커서 ID")
                    )
                )
            )
    }

    @Test
    fun findOrder() {
        every { orderService.findOrderByUser(id = orderId, userId = userId) } returns orderDetailResult

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/customer/orders/{id}", orderId)
                .header("Authorization", "Bearer access-token")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "customer-order-detail",
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
    fun cancelOder() {
        every { orderService.cancelOrder(id = orderId) } returns orderDetailResult

        mockMvc.perform(
            RestDocumentationRequestBuilders.patch("/customer/orders/{id}", orderId)
                .header("Authorization", "Bearer access-token")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "customer-order-cancel",
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
}
