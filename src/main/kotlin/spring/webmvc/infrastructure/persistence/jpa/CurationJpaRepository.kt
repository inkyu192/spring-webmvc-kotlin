package spring.webmvc.infrastructure.persistence.jpa

import org.springframework.data.jpa.repository.JpaRepository
import spring.webmvc.domain.model.entity.Curation
import spring.webmvc.domain.model.enums.CurationCategory

interface CurationJpaRepository : JpaRepository<Curation, Long> {

    fun findByCategoryAndIsExposedIsTrueOrderBySortOrder(category: CurationCategory): List<Curation>
}