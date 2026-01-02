package spring.webmvc.infrastructure.persistence.adapter

import org.springframework.stereotype.Component
import spring.webmvc.domain.model.entity.Curation
import spring.webmvc.domain.model.enums.CurationCategory
import spring.webmvc.domain.repository.CurationRepository
import spring.webmvc.infrastructure.extensions.findByIdOrThrow
import spring.webmvc.infrastructure.persistence.jpa.CurationJpaRepository

@Component
class CurationRepositoryAdapter(
    private val jpaRepository: CurationJpaRepository,
) : CurationRepository {
    override fun findById(id: Long): Curation = jpaRepository.findByIdOrThrow(id)

    override fun findExposed() = jpaRepository.findByIsExposedIsTrueOrderBySortOrder()

    override fun findByCategory(category: CurationCategory) =
        jpaRepository.findByCategoryAndIsExposedIsTrueOrderBySortOrder(category)

    override fun save(curation: Curation): Curation = jpaRepository.save(curation)
}