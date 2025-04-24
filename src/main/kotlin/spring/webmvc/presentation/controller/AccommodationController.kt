package spring.webmvc.presentation.controller

import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import spring.webmvc.application.service.AccommodationService
import spring.webmvc.presentation.dto.request.AccommodationCreateRequest
import spring.webmvc.presentation.dto.request.AccommodationUpdateRequest

@RestController
@RequestMapping("/products/accommodations")
class AccommodationController(
    private val accommodationService: AccommodationService,
) {
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_READER')")
    fun findAccommodation(@PathVariable id: Long) = accommodationService.findAccommodation(id)

    @PostMapping
    @PreAuthorize("hasAuthority('PRODUCT_WRITER')")
    @ResponseStatus(HttpStatus.CREATED)
    fun createAccommodation(@RequestBody @Validated accommodationCreateRequest: AccommodationCreateRequest) =
        accommodationService.createAccommodation(accommodationCreateRequest)

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_WRITER')")
    fun updateAccommodation(
        @PathVariable id: Long,
        @RequestBody @Validated accommodationUpdateRequest: AccommodationUpdateRequest
    ) = accommodationService.updateAccommodation(id = id, accommodationUpdateRequest = accommodationUpdateRequest)

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_WRITER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteAccommodation(@PathVariable id: Long) {
        accommodationService.deleteAccommodation(id)
    }
}