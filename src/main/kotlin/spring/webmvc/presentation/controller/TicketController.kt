package spring.webmvc.presentation.controller

import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import spring.webmvc.application.service.TicketService

@RestController
@RequestMapping("/products/tickets")
class TicketController(
    private val ticketService: TicketService,
) {
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_WRITER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteTicket(@PathVariable id: Long) {
        ticketService.deleteTicket(id)
    }
}