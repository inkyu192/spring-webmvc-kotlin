package spring.webmvc.presentation.controller

import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import spring.webmvc.application.service.FlightService
import spring.webmvc.presentation.dto.request.FlightCreateRequest
import spring.webmvc.presentation.dto.request.FlightUpdateRequest
import spring.webmvc.presentation.dto.response.FlightResponse

@RestController
@RequestMapping("/products/flights")
class FlightController(
    private val flightService: FlightService,
) {
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_READER')")
    fun findFlight(@PathVariable id: Long) = FlightResponse(flightDto = flightService.findFlight(id))

    @PostMapping
    @PreAuthorize("hasAuthority('PRODUCT_WRITER')")
    @ResponseStatus(HttpStatus.CREATED)
    fun createFlight(@RequestBody @Validated flightCreateRequest: FlightCreateRequest) =
        FlightResponse(
            flight = flightService.createFlight(
                name = flightCreateRequest.name,
                description = flightCreateRequest.description,
                price = flightCreateRequest.price,
                quantity = flightCreateRequest.quantity,
                airline = flightCreateRequest.airline,
                flightNumber = flightCreateRequest.flightNumber,
                departureAirport = flightCreateRequest.departureAirport,
                arrivalAirport = flightCreateRequest.arrivalAirport,
                departureTime = flightCreateRequest.departureTime,
                arrivalTime = flightCreateRequest.arrivalTime,
            )
        )

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_WRITER')")
    fun updateFlight(
        @PathVariable id: Long,
        @RequestBody @Validated flightUpdateRequest: FlightUpdateRequest
    ) = FlightResponse(
        flight = flightService.updateFlight(
            id = id,
            name = flightUpdateRequest.name,
            description = flightUpdateRequest.description,
            price = flightUpdateRequest.price,
            quantity = flightUpdateRequest.quantity,
            airline = flightUpdateRequest.airline,
            flightNumber = flightUpdateRequest.flightNumber,
            departureAirport = flightUpdateRequest.departureAirport,
            arrivalAirport = flightUpdateRequest.arrivalAirport,
            departureTime = flightUpdateRequest.departureTime,
            arrivalTime = flightUpdateRequest.arrivalTime,
        )
    )

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_WRITER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteFlight(@PathVariable id: Long) {
        flightService.deleteFlight(id)
    }
}