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
import spring.webmvc.application.service.FlightService
import spring.webmvc.domain.model.entity.Flight
import spring.webmvc.infrastructure.config.WebMvcTestConfig
import java.time.Instant
import java.time.temporal.ChronoUnit

@WebMvcTest(FlightController::class)
@Import(WebMvcTestConfig::class)
@ExtendWith(RestDocumentationExtension::class)
class FlightControllerTest() {
    @MockitoBean
    private lateinit var flightService: FlightService

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
    fun updateFlight() {
        val flightId = 1L
        val name = "name"
        val description = "description"
        val price = 1000
        val quantity = 5
        val airline = "airline"
        val flightNumber = "flightNumber"
        val departureAirport = "departureAirport"
        val arrivalAirport = "arrivalAirport"
        val departureTime = Instant.now()
        val arrivalTime = Instant.now().plus(1, ChronoUnit.HOURS)

        val flight = spy(
            Flight.create(
                name = name,
                description = description,
                price = price,
                quantity = quantity,
                airline = airline,
                flightNumber = flightNumber,
                departureAirport = departureAirport,
                arrivalAirport = arrivalAirport,
                departureTime = departureTime,
                arrivalTime = arrivalTime,
            )
        ).apply { whenever(id).thenReturn(1L) }

        val product = spy(flight.product)
            .apply { whenever(id).thenReturn(1L) }

        whenever(flight.product).thenReturn(product)
        whenever(
            flightService.updateFlight(
                id = flightId,
                name = name,
                description = description,
                price = price,
                quantity = quantity,
                airline = airline,
                flightNumber = flightNumber,
                departureAirport = departureAirport,
                arrivalAirport = arrivalAirport,
                departureTime = departureTime,
                arrivalTime = arrivalTime,
            )
        ).thenReturn(flight)

        mockMvc.perform(
            RestDocumentationRequestBuilders.patch("/products/flights/{id}", flightId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer access-token")
                .content(
                    """
                        {
                          "name": "$name",
                          "description": "$description",
                          "price": $price,
                          "quantity": $quantity,
                          "airline": "$airline",
                          "flightNumber": "$flightNumber",
                          "departureAirport": "$departureAirport",
                          "arrivalAirport": "$arrivalAirport",
                          "departureTime": "$departureTime",
                          "arrivalTime": "$arrivalTime"
                        }
                    """.trimIndent()
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "flight-update",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("id").description("아이디")
                    ),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("name").description("항공편명"),
                        PayloadDocumentation.fieldWithPath("description").description("설명"),
                        PayloadDocumentation.fieldWithPath("price").description("가격"),
                        PayloadDocumentation.fieldWithPath("quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("airline").description("항공사"),
                        PayloadDocumentation.fieldWithPath("flightNumber").description("항공편 ID"),
                        PayloadDocumentation.fieldWithPath("departureAirport").description("출발 공항"),
                        PayloadDocumentation.fieldWithPath("arrivalAirport").description("도착 공항"),
                        PayloadDocumentation.fieldWithPath("departureTime").description("출발 시간"),
                        PayloadDocumentation.fieldWithPath("arrivalTime").description("도착 시간")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("id").description("아이디"),
                        PayloadDocumentation.fieldWithPath("category").description("카테고리"),
                        PayloadDocumentation.fieldWithPath("name").description("항공편명"),
                        PayloadDocumentation.fieldWithPath("description").description("설명"),
                        PayloadDocumentation.fieldWithPath("price").description("가격"),
                        PayloadDocumentation.fieldWithPath("quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("createdAt").description("생성일시"),
                        PayloadDocumentation.fieldWithPath("flightId").description("항공아이디"),
                        PayloadDocumentation.fieldWithPath("airline").description("항공사"),
                        PayloadDocumentation.fieldWithPath("flightNumber").description("항공편 ID"),
                        PayloadDocumentation.fieldWithPath("departureAirport").description("출발 공항"),
                        PayloadDocumentation.fieldWithPath("arrivalAirport").description("도착 공항"),
                        PayloadDocumentation.fieldWithPath("departureTime").description("출발 시간"),
                        PayloadDocumentation.fieldWithPath("arrivalTime").description("도착 시간")
                    )
                )
            )
    }

    @Test
    fun deleteFlight() {
        val flightId = 1L

        doNothing().whenever(flightService).deleteFlight(flightId)
        doNothing().`when`(flightService).deleteFlight(flightId)

        mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/products/flights/{id}", flightId)
                .header("Authorization", "Bearer access-token")
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent())
            .andDo(
                MockMvcRestDocumentation.document(
                    "flight-delete",
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
