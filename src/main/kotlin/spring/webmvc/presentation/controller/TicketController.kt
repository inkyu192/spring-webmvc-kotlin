package spring.webmvc.presentation.controller

import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import spring.webmvc.application.service.TicketService
import spring.webmvc.presentation.dto.request.TicketCreateRequest
import spring.webmvc.presentation.dto.request.TicketUpdateRequest

@RestController
@RequestMapping("/products/tickets")
class TicketController(
    private val ticketService: TicketService,
) {
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_READER')")
    fun findTicket(@PathVariable id: Long) = ticketService.findTicket(id)

    @PostMapping
    @PreAuthorize("hasAuthority('PRODUCT_WRITER')")
    @ResponseStatus(HttpStatus.CREATED)
    fun createTicket(@RequestBody @Validated ticketCreateRequest: TicketCreateRequest) =
        ticketService.createTicket(ticketCreateRequest)

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_WRITER')")
    fun updateTicket(
        @PathVariable id: Long,
        @RequestBody @Validated ticketUpdateRequest: TicketUpdateRequest
    ) = ticketService.updateTicket(id = id, ticketUpdateRequest = ticketUpdateRequest)

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_WRITER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteTicket(@PathVariable id: Long) {
        ticketService.deleteTicket(id)
    }
}