package spring.webmvc.presentation.controller

import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import spring.webmvc.application.service.FlightService
import spring.webmvc.presentation.dto.request.FlightCreateRequest
import spring.webmvc.presentation.dto.request.FlightUpdateRequest

@RestController
@RequestMapping("/products/flights")
class FlightController(
    private val flightService: FlightService,
) {
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_READER')")
    fun findFlight(@PathVariable id: Long) = flightService.findFlight(id)

    @PostMapping
    @PreAuthorize("hasAuthority('PRODUCT_WRITER')")
    @ResponseStatus(HttpStatus.CREATED)
    fun createFlight(@RequestBody @Validated flightCreateRequest: FlightCreateRequest) =
        flightService.createFlight(flightCreateRequest)

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_WRITER')")
    fun updateFlight(
        @PathVariable id: Long,
        @RequestBody @Validated flightUpdateRequest: FlightUpdateRequest
    ) = flightService.updateFlight(id = id, flightUpdateRequest = flightUpdateRequest)

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_WRITER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteFlight(@PathVariable id: Long) {
        flightService.deleteFlight(id)
    }
}