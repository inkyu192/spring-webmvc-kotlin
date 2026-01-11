package spring.webmvc.infrastructure.persistence.adapter

import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import spring.webmvc.domain.repository.CurationProductRepository
import spring.webmvc.infrastructure.persistence.jpa.CurationProductQuerydslRepository

@Component
class CurationProductRepositoryAdapter(
    private val querydslRepository: CurationProductQuerydslRepository,
) : CurationProductRepository {
    override fun findAllWithCursorPage(curationId: Long?, cursorId: Long?) =
        querydslRepository.findAllWithCursorPage(curationId = curationId, cursorId = cursorId)

    override fun findAllWithOffsetPage(curationId: Long?, pageable: Pageable) =
        querydslRepository.findAllWithOffsetPage(curationId = curationId, pageable = pageable)
}