package spring.webmvc.presentation.controller

import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import spring.webmvc.application.service.AccommodationService
import spring.webmvc.presentation.dto.request.AccommodationUpdateRequest
import spring.webmvc.presentation.dto.response.AccommodationResponse

@RestController
@RequestMapping("/products/accommodations")
class AccommodationController(
    private val accommodationService: AccommodationService,
) {
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