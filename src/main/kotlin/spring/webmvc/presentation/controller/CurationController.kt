package spring.webmvc.presentation.controller

import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import spring.webmvc.application.dto.command.CurationCreateCommand
import spring.webmvc.application.service.CurationService
import spring.webmvc.presentation.dto.request.CurationCreateRequest
import spring.webmvc.presentation.dto.response.CurationResponse
import spring.webmvc.presentation.dto.response.ProductResponse

@RestController
@RequestMapping("/curations")
class CurationController(
    private val curationService: CurationService
) {
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.CREATED)
    fun createCuration(@Valid @RequestBody request: CurationCreateRequest): CurationResponse {
        val command = request.toCommand()
        return CurationResponse(curationResult = curationService.createCuration(command))
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    fun findCurations() =
        curationService.findCurations()
            .map { CurationResponse(curationResult = it) }
            .toList()

    @GetMapping("/{id}")
    fun findCuration(@PageableDefault pageable: Pageable, @PathVariable id: Long) =
        curationService.findCurationProduct(pageable = pageable, id = id)
            .map { ProductResponse(productResult = it) }
}