package spring.webmvc.presentation.controller

import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import spring.webmvc.application.service.FlightService

@RestController
@RequestMapping("/products/flights")
class FlightController(
    private val flightService: FlightService,
) {
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_WRITER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteFlight(@PathVariable id: Long) {
        flightService.deleteFlight(id)
    }
}