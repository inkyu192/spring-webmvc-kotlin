package spring.webmvc.presentation.controller

import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import spring.webmvc.application.service.AccommodationService
import spring.webmvc.presentation.dto.request.AccommodationCreateRequest
import spring.webmvc.presentation.dto.request.AccommodationUpdateRequest
import spring.webmvc.presentation.dto.response.AccommodationResponse

@RestController
@RequestMapping("/products/accommodations")
class AccommodationController(
    private val accommodationService: AccommodationService,
) {
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_READER')")
    fun findAccommodation(@PathVariable id: Long) =
        AccommodationResponse(accommodationDto = accommodationService.findAccommodation(id))

    @PostMapping
    @PreAuthorize("hasAuthority('PRODUCT_WRITER')")
    @ResponseStatus(HttpStatus.CREATED)
    fun createAccommodation(@RequestBody @Validated accommodationCreateRequest: AccommodationCreateRequest) =
        AccommodationResponse(
            accommodation = accommodationService.createAccommodation(
                name = accommodationCreateRequest.name,
                description = accommodationCreateRequest.description,
                price = accommodationCreateRequest.price,
                quantity = accommodationCreateRequest.quantity,
                place = accommodationCreateRequest.place,
                checkInTime = accommodationCreateRequest.checkInTime,
                checkOutTime = accommodationCreateRequest.checkOutTime,
            )
        )

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_WRITER')")
    fun updateAccommodation(
        @PathVariable id: Long,
        @RequestBody @Validated accommodationUpdateRequest: AccommodationUpdateRequest
    ) = AccommodationResponse(
        accommodation = accommodationService.updateAccommodation(
            id = id,
            name = accommodationUpdateRequest.name,
            description = accommodationUpdateRequest.description,
            price = accommodationUpdateRequest.price,
            quantity = accommodationUpdateRequest.quantity,
            place = accommodationUpdateRequest.place,
            checkInTime = accommodationUpdateRequest.checkInTime,
            checkOutTime = accommodationUpdateRequest.checkOutTime,
        )
    )

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_WRITER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteAccommodation(@PathVariable id: Long) {
        accommodationService.deleteAccommodation(id)
    }
}