package spring.webmvc.infrastructure.persistence.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import spring.webmvc.domain.model.entity.Curation

interface CurationJpaRepository : JpaRepository<Curation, Long> {

    @Query(
        """
		select c
		from Curation c
		where c.isExposed = true
		order by c.sortOrder
		"""
    )
    fun findExposed(): List<Curation>
}