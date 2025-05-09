package spring.webmvc.presentation.controller

import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import spring.webmvc.application.service.TicketService
import spring.webmvc.presentation.dto.request.TicketUpdateRequest
import spring.webmvc.presentation.dto.response.TicketResponse

@RestController
@RequestMapping("/products/tickets")
class TicketController(
    private val ticketService: TicketService,
) {
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