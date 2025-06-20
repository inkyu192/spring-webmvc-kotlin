package spring.webmvc.domain.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import spring.webmvc.domain.model.entity.CurationProduct

interface CurationProductRepository {
    fun findAllByCurationId(pageable: Pageable, curationId: Long): Page<CurationProduct>
}