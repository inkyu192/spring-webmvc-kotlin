package spring.webmvc.domain.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import spring.webmvc.domain.dto.CursorPage
import spring.webmvc.domain.model.entity.CurationProduct

interface CurationProductRepository {
    fun findAllWithCursorPage(curationId: Long?, cursorId: Long?): CursorPage<CurationProduct>
    fun findAllWithOffsetPage(curationId: Long?, pageable: Pageable): Page<CurationProduct>
}
