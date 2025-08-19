package spring.webmvc.infrastructure.persistence.adapter

import org.springframework.stereotype.Component
import spring.webmvc.domain.repository.CurationProductRepository
import spring.webmvc.infrastructure.persistence.jpa.CurationProductQuerydslRepository

@Component
class CurationProductRepositoryAdapter(
    private val querydslRepository: CurationProductQuerydslRepository,
) : CurationProductRepository {
    override fun findAll(curationId: Long, cursorId: Long?, size: Int) =
        querydslRepository.findAll(curationId = curationId, cursorId = cursorId, size = size)
}