package spring.webmvc.presentation.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
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
import spring.webmvc.presentation.dto.request.FlightCreateRequest
import spring.webmvc.presentation.dto.request.FlightUpdateRequest
import java.time.Instant
import java.time.temporal.ChronoUnit

@WebMvcTest(FlightController::class)
@Import(WebMvcTestConfig::class)
@ExtendWith(RestDocumentationExtension::class)
class FlightControllerTest(
    @Autowired private val objectMapper: ObjectMapper,
) {
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
    fun createFlight() {
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

        val request = FlightCreateRequest(
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
        val flight = Mockito.spy(
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
        ).apply { Mockito.`when`(id).thenReturn(1L) }

        val product = Mockito.spy(flight.product)
            .apply { Mockito.`when`(id).thenReturn(1L) }

        Mockito.`when`(flight.product).thenReturn(product)
        Mockito.`when`(
            flightService.createFlight(
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
            RestDocumentationRequestBuilders.post("/products/flights")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", "Bearer access-token")
        )
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andDo(
                MockMvcRestDocumentation.document(
                    "flight-create",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
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

        val request = FlightUpdateRequest(
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
        val flight = Mockito.spy(
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
        ).apply { Mockito.`when`(id).thenReturn(1L) }

        val product = Mockito.spy(flight.product)
            .apply { Mockito.`when`(id).thenReturn(1L) }

        Mockito.`when`(flight.product).thenReturn(product)
        Mockito.`when`(
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
                .content(objectMapper.writeValueAsString(request))
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

        Mockito.doNothing().`when`(flightService).deleteFlight(flightId)

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
