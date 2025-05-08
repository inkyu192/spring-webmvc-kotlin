package spring.webmvc.presentation.controller

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
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
import spring.webmvc.application.dto.command.OrderCreateCommand
import spring.webmvc.application.dto.command.OrderProductCreateCommand
import spring.webmvc.application.service.OrderService
import spring.webmvc.domain.model.entity.Order
import spring.webmvc.domain.model.entity.OrderProduct
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.model.enums.OrderStatus
import spring.webmvc.infrastructure.config.WebMvcTestConfig
import java.time.Instant

@WebMvcTest(OrderController::class)
@Import(WebMvcTestConfig::class)
@ExtendWith(RestDocumentationExtension::class)
class OrderControllerTest() {
    @MockitoBean
    private lateinit var orderService: OrderService

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
    fun createOrder() {
        val productId = 1L
        val quantity = 3

        val orderProductCreateCommand = OrderProductCreateCommand(productId = productId, quantity = quantity)
        val orderCreateCommand = OrderCreateCommand(products = listOf(orderProductCreateCommand))

        val order = Mockito.mock<Order>()
        val product = Mockito.mock<Product>()
        val orderProduct = Mockito.mock<OrderProduct>()

        Mockito.`when`(order.id).thenReturn(1L)
        Mockito.`when`(order.orderedAt).thenReturn(Instant.now())
        Mockito.`when`(order.status).thenReturn(OrderStatus.ORDER)
        Mockito.`when`(order.orderProducts).thenReturn(listOf(orderProduct))
        Mockito.`when`(product.name).thenReturn("name")
        Mockito.`when`(orderProduct.quantity).thenReturn(3)
        Mockito.`when`(orderProduct.orderPrice).thenReturn(5000)
        Mockito.`when`(orderProduct.product).thenReturn(product)
        Mockito.`when`(orderService.createOrder(orderCreateCommand)).thenReturn(order)

        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer access-token")
                .content(
                    """
                        {
                          "products": [
                            {
                              "productId": $productId,
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
                    "order-create",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("products[].productId").description("상품아이디"),
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
        val pageable: Pageable = PageRequest.of(0, 10)
        val memberId = 1L
        val orderStatus = OrderStatus.ORDER

        val order = Mockito.mock<Order>()
        val product = Mockito.mock<Product>()
        val orderProduct = Mockito.mock<OrderProduct>()

        val response = listOf(order)
        val page = PageImpl(response, pageable, response.size.toLong())

        Mockito.`when`(order.id).thenReturn(1L)
        Mockito.`when`(order.orderedAt).thenReturn(Instant.now())
        Mockito.`when`(order.status).thenReturn(OrderStatus.ORDER)
        Mockito.`when`(order.orderProducts).thenReturn(listOf(orderProduct))
        Mockito.`when`(product.name).thenReturn("name")
        Mockito.`when`(orderProduct.quantity).thenReturn(3)
        Mockito.`when`(orderProduct.orderPrice).thenReturn(5000)
        Mockito.`when`(orderProduct.product).thenReturn(product)
        Mockito.`when`(orderService.findOrders(pageable = pageable, orderStatus = orderStatus)).thenReturn(page)

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/orders")
                .header("Authorization", "Bearer access-token")
                .param("page", pageable.pageNumber.toString())
                .param("size", pageable.pageSize.toString())
                .param("memberId", memberId.toString())
                .param("orderStatus", orderStatus.toString())
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "order-list",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    RequestDocumentation.queryParameters(
                        RequestDocumentation.parameterWithName("page").description("페이지 번호").optional(),
                        RequestDocumentation.parameterWithName("size").description("페이지 크기").optional(),
                        RequestDocumentation.parameterWithName("memberId").description("회원아아디").optional(),
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
        val orderId = 1L

        val order = Mockito.mock<Order>()
        val product = Mockito.mock<Product>()
        val orderProduct = Mockito.mock<OrderProduct>()

        Mockito.`when`(order.id).thenReturn(1L)
        Mockito.`when`(order.orderedAt).thenReturn(Instant.now())
        Mockito.`when`(order.status).thenReturn(OrderStatus.ORDER)
        Mockito.`when`(order.orderProducts).thenReturn(listOf(orderProduct))
        Mockito.`when`(product.name).thenReturn("name")
        Mockito.`when`(orderProduct.quantity).thenReturn(3)
        Mockito.`when`(orderProduct.orderPrice).thenReturn(5000)
        Mockito.`when`(orderProduct.product).thenReturn(product)
        Mockito.`when`(orderService.findOrder(id = orderId)).thenReturn(order)

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/orders/{id}", orderId)
                .header("Authorization", "Bearer access-token")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "order-get",
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
        val orderId = 1L

        val order = Mockito.mock<Order>()
        Mockito.`when`(order.orderedAt).thenReturn(Instant.now())
        Mockito.`when`(order.status).thenReturn(OrderStatus.ORDER)

        val product = Mockito.mock<Product>()
        Mockito.`when`(product.name).thenReturn("name")

        val orderProduct = Mockito.mock<OrderProduct>()

        Mockito.`when`(orderProduct.product).thenReturn(product)
        Mockito.`when`(order.orderProducts).thenReturn(listOf(orderProduct))

        Mockito.`when`(orderService.cancelOrder(id = orderId)).thenReturn(order)

        mockMvc.perform(
            RestDocumentationRequestBuilders.patch("/orders/{id}", orderId)
                .header("Authorization", "Bearer access-token")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "order-cancel",
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
