package spring.webmvc.presentation.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.Page
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
import spring.webmvc.application.service.OrderService
import spring.webmvc.domain.model.enums.OrderStatus
import spring.webmvc.presentation.dto.request.OrderProductCreateRequest
import spring.webmvc.presentation.dto.request.OrderCreateRequest
import spring.webmvc.presentation.dto.response.OrderProductResponse
import spring.webmvc.presentation.dto.response.OrderResponse
import spring.webmvc.infrastructure.config.WebMvcTestConfig
import java.time.Instant

@WebMvcTest(OrderController::class)
@Import(WebMvcTestConfig::class)
@ExtendWith(RestDocumentationExtension::class)
class OrderControllerTest(
    @Autowired private val objectMapper: ObjectMapper,
) {
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
    @Throws(Exception::class)
    fun saveOrder() {
        val request = OrderCreateRequest(
            memberId = 1L,
            city = "city",
            street = "street",
            zipcode = "zipcode",
            products = listOf(OrderProductCreateRequest(1L, 3))
        )

        val response = OrderResponse(
            id = 1L,
            name = "name",
            orderedAt = Instant.now(),
            status = OrderStatus.ORDER,
            orderProducts = listOf(OrderProductResponse("name", 1000, 3))
        )

        Mockito.`when`(orderService.createOrder(request)).thenReturn(response)

        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer access-token")
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andDo(
                MockMvcRestDocumentation.document(
                    "order-create",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("memberId").description("회원아이디"),
                        PayloadDocumentation.fieldWithPath("city").description("city"),
                        PayloadDocumentation.fieldWithPath("street").description("street"),
                        PayloadDocumentation.fieldWithPath("zipcode").description("우편번호"),
                        PayloadDocumentation.fieldWithPath("orderProducts[].productId").description("상품아이디"),
                        PayloadDocumentation.fieldWithPath("orderProducts[].count").description("주문수량")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("id").description("주문아이디"),
                        PayloadDocumentation.fieldWithPath("name").description("회원명"),
                        PayloadDocumentation.fieldWithPath("orderedAt").description("주문일시"),
                        PayloadDocumentation.fieldWithPath("status").description("주문상태"),
                        PayloadDocumentation.fieldWithPath("orderProducts[].productName").description("상품명"),
                        PayloadDocumentation.fieldWithPath("orderProducts[].orderPrice").description("주문가격"),
                        PayloadDocumentation.fieldWithPath("orderProducts[].count").description("주문수량")
                    )
                )
            )
    }

    @Test
    @Throws(Exception::class)
    fun findOrders() {
        val pageable: Pageable = PageRequest.of(0, 10)
        val memberId = 1L
        val orderStatus = OrderStatus.ORDER

        val response = listOf(
            OrderResponse(
                id = 1L,
                name = "name",
                orderedAt = Instant.now(),
                status = OrderStatus.ORDER,
                orderProducts = listOf(OrderProductResponse(productName = "name", orderPrice = 1000, quantity = 3))
            )
        )
        val page: Page<OrderResponse> = PageImpl(response, pageable, response.size.toLong())

        Mockito.`when`(orderService.findOrders(memberId = memberId, orderStatus = orderStatus, pageable = pageable))
            .thenReturn(page)

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
                        PayloadDocumentation.fieldWithPath("content[].name").description("회원명"),
                        PayloadDocumentation.fieldWithPath("content[].orderedAt").description("주문일시"),
                        PayloadDocumentation.fieldWithPath("content[].status").description("주문상태"),
                        PayloadDocumentation.fieldWithPath("content[].orderProducts[].productName").description("상품명"),
                        PayloadDocumentation.fieldWithPath("content[].orderProducts[].orderPrice").description("주문가격"),
                        PayloadDocumentation.fieldWithPath("content[].orderProducts[].count").description("주문수량"),

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
    @Throws(Exception::class)
    fun findOrder() {
        val requestId = 1L

        val response = OrderResponse(
            id = 1L,
            name = "name",
            orderedAt = Instant.now(),
            status = OrderStatus.ORDER,
            orderProducts = listOf(OrderProductResponse(productName = "name", orderPrice = 1000, quantity = 3))
        )

        Mockito.`when`(orderService.findOrder(requestId)).thenReturn(response)

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/orders/{id}", requestId)
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
                        PayloadDocumentation.fieldWithPath("name").description("회원명"),
                        PayloadDocumentation.fieldWithPath("orderedAt").description("주문일시"),
                        PayloadDocumentation.fieldWithPath("status").description("주문상태"),
                        PayloadDocumentation.fieldWithPath("orderProducts[].productName").description("상품명"),
                        PayloadDocumentation.fieldWithPath("orderProducts[].orderPrice").description("주문가격"),
                        PayloadDocumentation.fieldWithPath("orderProducts[].count").description("주문수량")
                    )
                )
            )
    }

    @Test
    @Throws(Exception::class)
    fun cancelOder() {
        val requestId = 1L

        val response = OrderResponse(
            id = 1L,
            name = "name",
            orderedAt = Instant.now(),
            status = OrderStatus.ORDER,
            orderProducts = listOf(OrderProductResponse(productName = "name", orderPrice = 1000, quantity = 3))
        )

        Mockito.`when`(orderService.cancelOrder(requestId)).thenReturn(response)

        mockMvc.perform(
            RestDocumentationRequestBuilders.patch("/orders/{id}", requestId)
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
                        PayloadDocumentation.fieldWithPath("name").description("회원명"),
                        PayloadDocumentation.fieldWithPath("orderedAt").description("주문일시"),
                        PayloadDocumentation.fieldWithPath("status").description("주문상태"),
                        PayloadDocumentation.fieldWithPath("orderProducts[].productName").description("상품명"),
                        PayloadDocumentation.fieldWithPath("orderProducts[].orderPrice").description("주문가격"),
                        PayloadDocumentation.fieldWithPath("orderProducts[].count").description("주문수량")
                    )
                )
            )
    }
}
