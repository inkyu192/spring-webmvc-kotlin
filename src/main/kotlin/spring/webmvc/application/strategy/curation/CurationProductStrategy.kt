package spring.webmvc.application.strategy.curation

import org.springframework.data.domain.Pageable
import spring.webmvc.application.dto.result.CurationCursorPageResult
import spring.webmvc.application.dto.result.CurationOffsetPageResult
import spring.webmvc.domain.model.entity.Curation
import spring.webmvc.domain.model.enums.CurationType

interface CurationProductStrategy {
    fun type(): CurationType
    fun findProductsWithCursorPage(curation: Curation, userId: Long?, cursorId: Long?): CurationCursorPageResult
    fun findProductsWithOffsetPage(curation: Curation, pageable: Pageable): CurationOffsetPageResult
}
