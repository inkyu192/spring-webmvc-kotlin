package spring.webmvc.domain.repository

import spring.webmvc.domain.model.entity.Curation
import spring.webmvc.domain.model.enums.CurationCategory

interface CurationRepository {
    fun findAllByCategory(category: CurationCategory): List<Curation>
    fun save(curation: Curation): Curation
}