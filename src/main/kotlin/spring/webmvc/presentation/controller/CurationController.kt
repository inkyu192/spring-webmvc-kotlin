package spring.webmvc.presentation.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import spring.webmvc.application.service.CurationService
import spring.webmvc.domain.model.enums.CurationCategory
import spring.webmvc.presentation.dto.request.CurationCreateRequest
import spring.webmvc.presentation.dto.response.CurationCursorPageResponse
import spring.webmvc.presentation.dto.response.CurationDetailResponse
import spring.webmvc.presentation.dto.response.CurationListResponse

@RestController
@RequestMapping("/curations")
class CurationController(
    private val curationService: CurationService,
) {
    @PostMapping
    @PreAuthorize("hasAuthority('CURATION_WRITE')")
    @ResponseStatus(HttpStatus.CREATED)
    fun createCuration(@Valid @RequestBody request: CurationCreateRequest): CurationDetailResponse {
        val command = request.toCommand()
        val result = curationService.createCuration(command = command)

        return CurationDetailResponse.from(result)
    }

    @GetMapping
    @PreAuthorize("hasAuthority('CURATION_READ')")
    fun findCurations(
        @RequestParam category: CurationCategory,
    ): CurationListResponse {
        val resultList = curationService.findCurations(category)

        return CurationListResponse.from(
            category = category,
            resultList = resultList,
        )
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CURATION_READ')")
    fun findCuration(
        @PathVariable id: Long,
        @RequestParam(required = false) cursorId: Long?,
    ): CurationCursorPageResponse {
        val page = curationService.findCurationProduct(
            curationId = id,
            cursorId = cursorId,
        )

        return CurationCursorPageResponse.from(page)
    }
}