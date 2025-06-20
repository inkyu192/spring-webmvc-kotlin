package spring.webmvc.infrastructure.persistence.adapter

import org.springframework.stereotype.Component
import spring.webmvc.domain.model.entity.Curation
import spring.webmvc.domain.repository.CurationRepository
import spring.webmvc.infrastructure.persistence.jpa.CurationJpaRepository

@Component
class CurationRepositoryAdapter(
    private val jpaRepository: CurationJpaRepository,
) : CurationRepository {
    override fun findExposed() = jpaRepository.findExposed()

    override fun save(curation: Curation) = jpaRepository.save(curation)
}