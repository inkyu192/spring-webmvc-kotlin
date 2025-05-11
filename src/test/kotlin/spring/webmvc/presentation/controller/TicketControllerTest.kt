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
import spring.webmvc.application.service.TicketService
import spring.webmvc.domain.model.entity.Ticket
import spring.webmvc.infrastructure.config.WebMvcTestConfig
import java.time.Instant

@WebMvcTest(TicketController::class)
@Import(WebMvcTestConfig::class)
@ExtendWith(RestDocumentationExtension::class)
class TicketControllerTest() {
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
    fun updateTicket() {
        val ticketId = 1L
        val name = "name"
        val description = "description"
        val price = 1000
        val quantity = 5
        val place = "place"
        val performanceTime = Instant.now()
        val duration = "duration"
        val ageLimit = "ageLimit"

        val ticket = spy(
            Ticket.create(
                name = name,
                description = description,
                price = price,
                quantity = quantity,
                place = place,
                performanceTime = performanceTime,
                duration = duration,
                ageLimit = ageLimit,
            )
        ).apply { whenever(id).thenReturn(1L) }

        val product = spy(ticket.product)
            .apply { whenever(id).thenReturn(1L) }

        whenever(ticket.product).thenReturn(product)
        whenever(
            ticketService.updateTicket(
                id = ticketId,
                name = name,
                description = description,
                price = price,
                quantity = quantity,
                place = place,
                performanceTime = performanceTime,
                duration = duration,
                ageLimit = ageLimit,
            )
        ).thenReturn(ticket)

        mockMvc.perform(
            RestDocumentationRequestBuilders.patch("/products/tickets/{id}", ticketId)
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
                          "performanceTime": "$performanceTime",
                          "duration": "$duration",
                          "ageLimit": "$ageLimit"
                        }
                    """.trimIndent()
                )
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
                        PayloadDocumentation.fieldWithPath("category").description("카테고리"),
                        PayloadDocumentation.fieldWithPath("name").description("티켓명"),
                        PayloadDocumentation.fieldWithPath("description").description("설명"),
                        PayloadDocumentation.fieldWithPath("price").description("가격"),
                        PayloadDocumentation.fieldWithPath("quantity").description("수량"),
                        PayloadDocumentation.fieldWithPath("createdAt").description("생성일시"),
                        PayloadDocumentation.fieldWithPath("ticketId").description("티켓아이디"),
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
        val ticketId = 1L

        doNothing().whenever(ticketService).deleteTicket(ticketId)

        mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/products/tickets/{id}", ticketId)
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
