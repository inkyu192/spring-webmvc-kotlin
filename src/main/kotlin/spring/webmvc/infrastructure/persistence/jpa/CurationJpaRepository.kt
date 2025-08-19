package spring.webmvc.infrastructure.persistence.jpa

import org.springframework.data.jpa.repository.JpaRepository
import spring.webmvc.domain.model.entity.Curation

interface CurationJpaRepository : JpaRepository<Curation, Long> {

    fun findByIsExposedIsTrueOrderBySortOrder(): List<Curation>
}