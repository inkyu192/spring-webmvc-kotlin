package spring.webmvc.domain.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import spring.webmvc.domain.model.entity.CurationProduct
import spring.webmvc.infrastructure.persistence.dto.CursorPage

interface CurationProductRepository {
    fun findAllWithCursorPage(curationId: Long?, cursorId: Long?): CursorPage<CurationProduct>
    fun findAllWithOffsetPage(curationId: Long?, pageable: Pageable): Page<CurationProduct>
}