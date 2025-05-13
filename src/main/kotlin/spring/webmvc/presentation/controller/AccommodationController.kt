package spring.webmvc.presentation.controller

import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import spring.webmvc.application.service.AccommodationService

@RestController
@RequestMapping("/products/accommodations")
class AccommodationController(
    private val accommodationService: AccommodationService,
) {
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_WRITER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteAccommodation(@PathVariable id: Long) {
        accommodationService.deleteAccommodation(id)
    }
}