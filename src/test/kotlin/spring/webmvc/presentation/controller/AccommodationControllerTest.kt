package spring.webmvc.presentation.controller

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.spy
import org.mockito.kotlin.whenever
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
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
import spring.webmvc.application.service.AccommodationService
import spring.webmvc.domain.model.entity.Accommodation
import spring.webmvc.infrastructure.config.WebMvcTestConfig
import java.time.Instant
import java.time.temporal.ChronoUnit

@WebMvcTest(AccommodationController::class)
@Import(WebMvcTestConfig::class)
@ExtendWith(RestDocumentationExtension::class)
class AccommodationControllerTest() {
    @MockitoBean
    private lateinit var accommodationService: AccommodationService

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
    fun updateAccommodation() {
        val accommodationId = 1L
        val name = "name"
        val description = "description"
        val price = 1000
        val quantity = 5
        val place = "place"
        val checkInTime = Instant.now()
        val checkOutTime = Instant.now().plus(1, ChronoUnit.DAYS)

        val accommodation = spy(
            Accommodation.create(
                name = name,
                description = description,
                price = price,
                quantity = quantity,
                place = place,
                checkInTime = checkInTime,
                checkOutTime = checkOutTime,
            )
        ).apply { whenever(id).thenReturn(1L) }

        val product = spy(accommodation.product)
            .apply { whenever(id).thenReturn(1L) }

        whenever(accommodation.product).thenReturn(product)
        whenever(
            accommodationService.updateAccommodation(
                id = accommodationId,
                name = name,
                description = description,
                price = price,
                quantity = quantity,
                place = place,
                checkInTime = checkInTime,
                checkOutTime = checkOutTime,
            )
        ).thenReturn(accommodation)

        mockMvc.perform(
            RestDocumentationRequestBuilders.patch("/products/accommodations/{id}", accommodationId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer access-token")
                .content(
                    """
                        {
                          "name": "$name",
                          "description": "$description",
                          "price": $price,
                          "quantity": $quantity,
                          "place": "$place",
                          "checkInTime": "$checkInTime",
                          "checkOutTime": "$checkOutTime"
                        }
                    """.trimIndent()
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "accommodation-update",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("id").description("아이디")
                    ),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("name").description("숙소명"),
                        PayloadDocumentation.fieldWithPath("description").description("설명"),
                        PayloadDocumentation.fieldWithPath("price").description("가격"),
                        PayloadDocumentation.fieldWithPath("quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("place").description("장소"),
                        PayloadDocumentation.fieldWithPath("checkInTime").description("체크인 시간"),
                        PayloadDocumentation.fieldWithPath("checkOutTime").description("체크아웃 시간")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("id").description("아이디"),
                        PayloadDocumentation.fieldWithPath("category").description("카테고리"),
                        PayloadDocumentation.fieldWithPath("name").description("숙소명"),
                        PayloadDocumentation.fieldWithPath("description").description("설명"),
                        PayloadDocumentation.fieldWithPath("price").description("가격"),
                        PayloadDocumentation.fieldWithPath("quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("createdAt").description("생성일시"),
                        PayloadDocumentation.fieldWithPath("accommodationId").description("숙소아이디"),
                        PayloadDocumentation.fieldWithPath("place").description("장소"),
                        PayloadDocumentation.fieldWithPath("checkInTime").description("체크인 시간"),
                        PayloadDocumentation.fieldWithPath("checkOutTime").description("체크아웃 시간")
                    )
                )
            )
    }

    @Test
    fun deleteAccommodation() {
        val accommodationId = 1L

        doNothing().whenever(accommodationService).deleteAccommodation(accommodationId)

        mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/products/accommodations/{id}", accommodationId)
                .header("Authorization", "Bearer access-token")
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent())
            .andDo(
                MockMvcRestDocumentation.document(
                    "accommodation-delete",
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
