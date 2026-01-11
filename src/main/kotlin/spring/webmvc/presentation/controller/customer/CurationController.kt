package spring.webmvc.presentation.controller.customer

import org.springframework.web.bind.annotation.*
import spring.webmvc.application.service.CurationService
import spring.webmvc.domain.model.enums.CurationCategory
import spring.webmvc.presentation.dto.response.CurationCursorPageResponse
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
        val resultList = curationService.findCurationsCached(category)

        return CurationListResponse.from(
            category = category,
            resultList = resultList,
        )
    }

    @GetMapping("/{id}")
    fun findCuration(
        @PathVariable id: Long,
        @RequestParam(required = false) cursorId: Long?,
    ): CurationCursorPageResponse {
        val page = curationService.findCurationProductWithCursorPageCached(
            curationId = id,
            cursorId = cursorId,
        )

        return CurationCursorPageResponse.from(page)
    }
}