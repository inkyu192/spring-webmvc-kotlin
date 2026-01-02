package spring.webmvc.presentation.controller.customer

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockkObject
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
import spring.webmvc.application.dto.command.OrderCancelCommand
import spring.webmvc.application.dto.command.OrderCreateCommand
import spring.webmvc.application.dto.command.OrderProductCreateCommand
import spring.webmvc.application.dto.query.OrderFindByIdQuery
import spring.webmvc.application.dto.query.OrderFindQuery
import spring.webmvc.application.dto.result.OrderProductResult
import spring.webmvc.application.dto.result.OrderResult
import spring.webmvc.application.service.OrderService
import spring.webmvc.domain.model.enums.OrderStatus
import spring.webmvc.infrastructure.config.ControllerTest
import spring.webmvc.infrastructure.security.SecurityContextUtil
import java.time.Instant

@ControllerTest([CustomerOrderController::class])
class CustomerOrderControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var orderService: OrderService
    private lateinit var orderResult: OrderResult
    private lateinit var orderProductResult: OrderProductResult
    private lateinit var orderProductCreateCommand: OrderProductCreateCommand
    private lateinit var orderCreateCommand: OrderCreateCommand
    private lateinit var pageable: Pageable
    private lateinit var page: PageImpl<OrderResult>
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

        orderResult = OrderResult(
            id = orderId,
            orderedAt = Instant.now(),
            status = OrderStatus.ORDER,
            products = listOf(orderProductResult)
        )

        pageable = PageRequest.of(0, 10)
        page = PageImpl(listOf(orderResult), pageable, 1)
    }

    @Test
    fun createOrder() {
        every { orderService.createOrder(command = orderCreateCommand) } returns orderResult

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
        val query = OrderFindQuery(userId = userId, pageable = pageable, orderStatus = orderStatus)
        every { orderService.findOrders(query = query) } returns page

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/customer/orders")
                .header("Authorization", "Bearer access-token")
                .queryParam("page", pageable.pageNumber.toString())
                .queryParam("size", pageable.pageSize.toString())
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
                        RequestDocumentation.parameterWithName("page").description("페이지 번호").optional(),
                        RequestDocumentation.parameterWithName("size").description("페이지 크기").optional(),
                        RequestDocumentation.parameterWithName("orderStatus").description("주문상태").optional()
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("content[].id").description("주문아이디"),
                        PayloadDocumentation.fieldWithPath("content[].orderedAt").description("주문일시"),
                        PayloadDocumentation.fieldWithPath("content[].status").description("주문상태"),
                        PayloadDocumentation.fieldWithPath("content[].products[].name").description("상품명"),
                        PayloadDocumentation.fieldWithPath("content[].products[].price").description("주문가격"),
                        PayloadDocumentation.fieldWithPath("content[].products[].quantity").description("주문수량"),

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
    fun findOrder() {
        val query = OrderFindByIdQuery(userId = userId, id = orderId)
        every { orderService.findOrder(query = query) } returns orderResult

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
        val command = OrderCancelCommand(userId = userId, id = orderId)
        every { orderService.cancelOrder(command = command) } returns orderResult

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
