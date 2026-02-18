package spring.webmvc.presentation.controller.customer

import org.springframework.web.bind.annotation.*
import spring.webmvc.application.service.CurationService
import spring.webmvc.domain.model.enums.CurationCategory
import spring.webmvc.infrastructure.security.SecurityContextUtil
import spring.webmvc.presentation.dto.response.CurationDetailCursorPageResponse
import spring.webmvc.presentation.dto.response.CurationListResponse

@RestController("customerCurationController")
@RequestMapping("/customer/curations")
class CurationController(
    private val curationService: CurationService,
) {
    @GetMapping
    fun findCurations(
        @RequestParam category: CurationCategory,
    ): CurationListResponse {
        val results = curationService.findCurationsCached(category)

        return CurationListResponse.of(results = results)
    }

    @GetMapping("/{id}")
    fun findCuration(
        @PathVariable id: Long,
        @RequestParam(required = false) cursorId: Long?,
    ): CurationDetailCursorPageResponse {
        val result = curationService.findCurationProductCached(
            userId = SecurityContextUtil.getUserIdOrNull(),
            curationId = id,
            cursorId = cursorId,
        )

        return CurationDetailCursorPageResponse.of(result)
    }
}
