package spring.webmvc.domain.repository

import spring.webmvc.domain.model.entity.Curation
import spring.webmvc.domain.model.entity.CurationProduct
import spring.webmvc.infrastructure.persistence.dto.CursorPage

interface CurationProductRepository {
    fun findAll(curation: Curation, cursorId: Long?): CursorPage<CurationProduct>
}