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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import spring.webmvc.application.service.TicketService
import spring.webmvc.infrastructure.config.WebMvcTestConfig
import spring.webmvc.presentation.dto.request.TicketCreateRequest
import spring.webmvc.presentation.dto.request.TicketUpdateRequest
import spring.webmvc.presentation.dto.response.TicketResponse
import java.time.Instant

@WebMvcTest(TicketController::class)
@Import(WebMvcTestConfig::class)
@ExtendWith(RestDocumentationExtension::class)
class TicketControllerTest(
    @Autowired private val objectMapper: ObjectMapper,
) {
    @MockitoBean
    private lateinit var ticketService: TicketService

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
    fun createTicket() {
        val request = TicketCreateRequest(
            name = "name",
            description = "description",
            price = 1000,
            quantity = 5,
            place = "place",
            performanceTime = Instant.now(),
            duration = "duration",
            ageLimit = "ageLimit",
        )
        val response = TicketResponse(
            id = 1L,
            name = "name",
            description = "description",
            price = 1000,
            quantity = 5,
            place = "place",
            performanceTime = Instant.now(),
            duration = "duration",
            ageLimit = "ageLimit",
            createdAt = Instant.now()
        )

        Mockito.`when`(ticketService.createTicket(request)).thenReturn(response)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/products/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", "Bearer accessToken")
        )
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andDo(
                MockMvcRestDocumentation.document(
                    "ticket-create",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("name").description("티켓명"),
                        PayloadDocumentation.fieldWithPath("description").description("설명"),
                        PayloadDocumentation.fieldWithPath("price").description("가격"),
                        PayloadDocumentation.fieldWithPath("quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("place").description("장소"),
                        PayloadDocumentation.fieldWithPath("performanceTime").description("공연 시간"),
                        PayloadDocumentation.fieldWithPath("duration").description("공연 시간"),
                        PayloadDocumentation.fieldWithPath("ageLimit").description("관람 연령")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("id").description("아이디"),
                        PayloadDocumentation.fieldWithPath("name").description("티켓명"),
                        PayloadDocumentation.fieldWithPath("description").description("설명"),
                        PayloadDocumentation.fieldWithPath("price").description("가격"),
                        PayloadDocumentation.fieldWithPath("quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("createdAt").description("생성일시"),
                        PayloadDocumentation.fieldWithPath("place").description("장소"),
                        PayloadDocumentation.fieldWithPath("performanceTime").description("공연 시간"),
                        PayloadDocumentation.fieldWithPath("duration").description("공연 시간"),
                        PayloadDocumentation.fieldWithPath("ageLimit").description("관람 연령")
                    )
                )
            )
    }

    @Test
    fun findTicket() {
        val requestId = 1L
        val response = TicketResponse(
            id = 1L,
            name = "name",
            description = "description",
            price = 1000,
            quantity = 5,
            createdAt = Instant.now(),
            place = "place",
            performanceTime = Instant.now(),
            duration = "duration",
            ageLimit = "ageLimit"
        )

        Mockito.`when`(ticketService.findTicket(requestId)).thenReturn(response)

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/products/tickets/{id}", requestId)
                .header("Authorization", "Bearer access-token")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "ticket-get",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("id").description("아이디")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("id").description("아이디"),
                        PayloadDocumentation.fieldWithPath("name").description("티켓명"),
                        PayloadDocumentation.fieldWithPath("description").description("설명"),
                        PayloadDocumentation.fieldWithPath("price").description("가격"),
                        PayloadDocumentation.fieldWithPath("quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("createdAt").description("생성일시"),
                        PayloadDocumentation.fieldWithPath("place").description("장소"),
                        PayloadDocumentation.fieldWithPath("performanceTime").description("공연 시간"),
                        PayloadDocumentation.fieldWithPath("duration").description("공연 시간"),
                        PayloadDocumentation.fieldWithPath("ageLimit").description("관람 연령")
                    )
                )
            )
    }

    @Test
    fun updateTicket() {
        val requestId = 1L
        val request = TicketUpdateRequest(
            name = "name",
            description = "description",
            price = 1000,
            quantity = 5,
            place = "place",
            performanceTime = Instant.now(),
            duration = "duration",
            ageLimit = "ageLimit"
        )
        val response = TicketResponse(
            id = 1L,
            name = "name",
            description = "description",
            price = 1000,
            quantity = 5,
            createdAt = Instant.now(),
            place = "place",
            performanceTime = Instant.now(),
            duration = "duration",
            ageLimit = "ageLimit"
        )

        Mockito.`when`(ticketService.updateTicket(id = requestId, ticketUpdateRequest = request)).thenReturn(response)

        mockMvc.perform(
            RestDocumentationRequestBuilders.patch("/products/tickets/{id}", requestId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer access-token")
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "ticket-update",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("id").description("아이디")
                    ),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("name").description("티켓명"),
                        PayloadDocumentation.fieldWithPath("description").description("설명"),
                        PayloadDocumentation.fieldWithPath("price").description("가격"),
                        PayloadDocumentation.fieldWithPath("quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("place").description("장소"),
                        PayloadDocumentation.fieldWithPath("performanceTime").description("공연 시간"),
                        PayloadDocumentation.fieldWithPath("duration").description("공연 시간"),
                        PayloadDocumentation.fieldWithPath("ageLimit").description("관람 연령")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("id").description("아이디"),
                        PayloadDocumentation.fieldWithPath("name").description("티켓명"),
                        PayloadDocumentation.fieldWithPath("description").description("설명"),
                        PayloadDocumentation.fieldWithPath("price").description("가격"),
                        PayloadDocumentation.fieldWithPath("quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("createdAt").description("생성일시"),
                        PayloadDocumentation.fieldWithPath("place").description("장소"),
                        PayloadDocumentation.fieldWithPath("performanceTime").description("공연 시간"),
                        PayloadDocumentation.fieldWithPath("duration").description("공연 시간"),
                        PayloadDocumentation.fieldWithPath("ageLimit").description("관람 연령")
                    )
                )
            )
    }

    @Test
    fun deleteTicket() {
        val requestId = 1L

        Mockito.doNothing().`when`(ticketService).deleteTicket(requestId)

        mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/products/tickets/{id}", requestId)
                .header("Authorization", "Bearer access-token")
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent())
            .andDo(
                MockMvcRestDocumentation.document(
                    "ticket-delete",
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
