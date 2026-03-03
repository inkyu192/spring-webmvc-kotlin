package spring.webmvc.infrastructure.persistence.adapter

import org.springframework.stereotype.Component
import spring.webmvc.domain.model.entity.Translation
import spring.webmvc.domain.repository.TranslationRepository
import spring.webmvc.infrastructure.persistence.jpa.TranslationJpaRepository

@Component
class TranslationRepositoryAdapter(
    private val jpaRepository: TranslationJpaRepository,
) : TranslationRepository {
    override fun findAll(): List<Translation> = jpaRepository.findAll()
}
