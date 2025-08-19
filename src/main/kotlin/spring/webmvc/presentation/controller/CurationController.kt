package spring.webmvc.presentation.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import spring.webmvc.application.service.CurationService
import spring.webmvc.presentation.dto.request.CurationCreateRequest
import spring.webmvc.presentation.dto.response.CurationCreateResponse
import spring.webmvc.presentation.dto.response.CurationProductResponse
import spring.webmvc.presentation.dto.response.CurationListResponse

@RestController
@RequestMapping("/curations")
class CurationController(
    private val curationService: CurationService,
) {
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.CREATED)
    fun createCuration(@Valid @RequestBody request: CurationCreateRequest) =
        CurationCreateResponse(
            id = curationService.createCuration(
                command = request.toCommand()
            )
        )

    @GetMapping
    fun findCurations() = CurationListResponse(resultList = curationService.findCurations())

    @GetMapping("/{id}")
    fun findCuration(
        @PathVariable id: Long,
        @RequestParam(required = false, defaultValue = "10") size: Int,
        @RequestParam(required = false) cursorId: Long?,
    ) = CurationProductResponse(
        result = curationService.findCurationProduct(
            curationId = id,
            size = size,
            cursorId = cursorId,
        )
    )
}