package spring.webmvc.infrastructure.persistence.jpa

import org.springframework.data.jpa.repository.JpaRepository
import spring.webmvc.domain.model.entity.Curation
import spring.webmvc.domain.model.enums.CurationPlacement

interface CurationJpaRepository : JpaRepository<Curation, Long> {

    fun findByPlacementAndIsExposedIsTrueOrderBySortOrder(placement: CurationPlacement): List<Curation>
}
