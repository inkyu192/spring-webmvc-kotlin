package spring.webmvc.presentation.controller

import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import spring.webmvc.application.service.TicketService
import spring.webmvc.presentation.dto.request.TicketCreateRequest
import spring.webmvc.presentation.dto.request.TicketUpdateRequest
import spring.webmvc.presentation.dto.response.TicketResponse

@RestController
@RequestMapping("/products/tickets")
class TicketController(
    private val ticketService: TicketService,
) {
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_READER')")
    fun findTicket(@PathVariable id: Long) = TicketResponse(ticketDto = ticketService.findTicket(id))

    @PostMapping
    @PreAuthorize("hasAuthority('PRODUCT_WRITER')")
    @ResponseStatus(HttpStatus.CREATED)
    fun createTicket(@RequestBody @Validated ticketCreateRequest: TicketCreateRequest) =
        TicketResponse(
            ticket = ticketService.createTicket(
                name = ticketCreateRequest.name,
                description = ticketCreateRequest.description,
                price = ticketCreateRequest.price,
                quantity = ticketCreateRequest.quantity,
                place = ticketCreateRequest.place,
                performanceTime = ticketCreateRequest.performanceTime,
                duration = ticketCreateRequest.duration,
                ageLimit = ticketCreateRequest.ageLimit,
            )
        )

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_WRITER')")
    fun updateTicket(
        @PathVariable id: Long,
        @RequestBody @Validated ticketUpdateRequest: TicketUpdateRequest
    ) = TicketResponse(
        ticket = ticketService.updateTicket(
            id = id,
            name = ticketUpdateRequest.name,
            description = ticketUpdateRequest.description,
            price = ticketUpdateRequest.price,
            quantity = ticketUpdateRequest.quantity,
            place = ticketUpdateRequest.place,
            performanceTime = ticketUpdateRequest.performanceTime,
            duration = ticketUpdateRequest.duration,
            ageLimit = ticketUpdateRequest.ageLimit,
        )
    )

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_WRITER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteTicket(@PathVariable id: Long) {
        ticketService.deleteTicket(id)
    }
}