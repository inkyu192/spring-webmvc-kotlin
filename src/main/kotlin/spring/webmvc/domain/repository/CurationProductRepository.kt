package spring.webmvc.domain.repository

import spring.webmvc.domain.model.entity.CurationProduct
import spring.webmvc.infrastructure.persistence.dto.CursorPage

interface CurationProductRepository {
    fun findAllWithCursorPage(curationId: Long?, cursorId: Long?): CursorPage<CurationProduct>
}