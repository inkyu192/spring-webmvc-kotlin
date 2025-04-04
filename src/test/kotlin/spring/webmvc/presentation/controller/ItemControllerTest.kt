package spring.webmvc.presentation.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import spring.webmvc.application.service.ItemService
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.infrastructure.config.security.JwtTokenProvider
import spring.webmvc.infrastructure.util.ResponseWriter
import spring.webmvc.presentation.dto.request.ItemSaveRequest
import spring.webmvc.presentation.dto.response.ItemResponse
import java.time.Instant

@WebMvcTest(ItemController::class)
@ExtendWith(RestDocumentationExtension::class)
class ItemControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var itemService: ItemService

    @MockitoBean
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @MockitoBean
    private lateinit var responseWriter: ResponseWriter

    @BeforeEach
    fun setUp(webApplicationContext: WebApplicationContext, restDocumentation: RestDocumentationContextProvider) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply<DefaultMockMvcBuilder>(MockMvcRestDocumentation.documentationConfiguration(restDocumentation))
            .build()
    }

    private val authorization =
        "Bearer eyJhbGciOiJIUzI1NiJ9.eyJtZW1iZXJJZCI6MSwicGVybWlzc2lvbnMiOlsiSVRFTV9SRUFERVIiLCJJVEVNX1dSSVRFUiJdLCJpYXQiOjE3NDM0ODUzMzYsImV4cCI6MTc0MzQ4NTkzNn0.tu5bcIGsTtmXy0RmqvKa3H-rWKBn3rB1i1RjPzlkTVs"

    @Test
    fun saveItemTest() {
        val request = ItemSaveRequest("상품명", "설명", 1000, 5, Category.ROLE_BOOK)
        val itemResponse = ItemResponse(1L, "상품명", "설명", 1000, 10, Instant.now())

        Mockito.`when`(itemService.saveItem(request)).thenReturn(itemResponse)

        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", authorization)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andDo(
                MockMvcRestDocumentation.document(
                    "item-create",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("name").description("상품명"),
                        PayloadDocumentation.fieldWithPath("description").description("설명"),
                        PayloadDocumentation.fieldWithPath("price").description("가격"),
                        PayloadDocumentation.fieldWithPath("quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("category").description("카테고리")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("id").description("아이디"),
                        PayloadDocumentation.fieldWithPath("name").description("상품명"),
                        PayloadDocumentation.fieldWithPath("description").description("설명"),
                        PayloadDocumentation.fieldWithPath("price").description("가격"),
                        PayloadDocumentation.fieldWithPath("quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("createdAt").description("생성일시")
                    )
                )
            )
    }

    @Test
    fun findItem() {
        val requestId = 1L
        val itemResponse = ItemResponse(1L, "item1", "description", 1000, 10, Instant.now())

        Mockito.`when`(itemService.findItem(requestId)).thenReturn(itemResponse)

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/items/{id}", requestId)
                .header("Authorization", authorization)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(
                MockMvcRestDocumentation.document(
                    "item-get",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("id").description("아이디")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("id").description("아이디"),
                        PayloadDocumentation.fieldWithPath("name").description("상품명"),
                        PayloadDocumentation.fieldWithPath("description").description("설명"),
                        PayloadDocumentation.fieldWithPath("price").description("가격"),
                        PayloadDocumentation.fieldWithPath("quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("createdAt").description("생성일시")
                    )
                )
            )
    }

    @Test
    fun findItems() {
        val pageable = PageRequest.of(0, 10)
        val name = "item"

        val itemResponse = listOf(
            ItemResponse(1L, "item1", "description", 1000, 10, Instant.now()),
            ItemResponse(2L, "item2", "description", 2000, 20, Instant.now()),
            ItemResponse(3L, "item3", "description", 3000, 30, Instant.now())
        )
        val page = PageImpl(itemResponse, pageable, itemResponse.size.toLong())

        Mockito.`when`(itemService.findItems(pageable, name)).thenReturn(page)

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/items")
                .header("Authorization", authorization)
                .param("page", "0")
                .param("size", "10")
                .param("name", name)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(
                MockMvcRestDocumentation.document(
                    "item-list",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    RequestDocumentation.queryParameters(
                        RequestDocumentation.parameterWithName("page").description("페이지 번호").optional(),
                        RequestDocumentation.parameterWithName("size").description("페이지 크기").optional(),
                        RequestDocumentation.parameterWithName("name").description("상품명").optional()
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("content[].id").description("아이디"),
                        PayloadDocumentation.fieldWithPath("content[].name").description("상품명"),
                        PayloadDocumentation.fieldWithPath("content[].description").description("설명"),
                        PayloadDocumentation.fieldWithPath("content[].price").description("가격"),
                        PayloadDocumentation.fieldWithPath("content[].quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("content[].createdAt").description("생성일시"),

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
    fun updateItem() {
        val requestId = 1L

        val request = ItemSaveRequest("상품명", "설명", 1000, 5, Category.ROLE_BOOK)
        val itemResponse = ItemResponse(1L, "상품명", "설명", 1000, 10, Instant.now())

        Mockito.`when`(itemService.putItem(requestId, request)).thenReturn(Pair(false, itemResponse))

        mockMvc.perform(
            RestDocumentationRequestBuilders.put("/items/{id}", requestId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authorization)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(
                MockMvcRestDocumentation.document(
                    "item-update",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("id").description("아이디")
                    ),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("name").description("상품명"),
                        PayloadDocumentation.fieldWithPath("description").description("설명"),
                        PayloadDocumentation.fieldWithPath("price").description("가격"),
                        PayloadDocumentation.fieldWithPath("quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("category").description("카테고리")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("id").description("아이디"),
                        PayloadDocumentation.fieldWithPath("name").description("상품명"),
                        PayloadDocumentation.fieldWithPath("description").description("설명"),
                        PayloadDocumentation.fieldWithPath("price").description("가격"),
                        PayloadDocumentation.fieldWithPath("quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("createdAt").description("생성일시")
                    )
                )
            )
    }

    @Test
    fun deleteItem() {
        val requestId = 1L

        Mockito.doNothing().`when`(itemService).deleteItem(requestId)

        mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/items/{id}", requestId)
                .header("Authorization", authorization)
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent)
            .andDo(
                MockMvcRestDocumentation.document(
                    "item-delete",
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
