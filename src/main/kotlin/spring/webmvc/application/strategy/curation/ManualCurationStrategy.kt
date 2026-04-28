package spring.webmvc.application.strategy.curation

import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import spring.webmvc.application.dto.result.CurationCursorPageResult
import spring.webmvc.application.dto.result.CurationOffsetPageResult
import spring.webmvc.domain.model.entity.Curation
import spring.webmvc.domain.model.enums.CurationType
import spring.webmvc.domain.repository.CurationProductRepository
import spring.webmvc.domain.repository.UserProductBadgeRepository

@Component
class ManualCurationStrategy(
    private val curationProductRepository: CurationProductRepository,
    private val userProductBadgeRepository: UserProductBadgeRepository,
) : CurationProductStrategy {

    override fun type(): CurationType = CurationType.MANUAL

    override fun findProductsWithCursorPage(
        curation: Curation,
        userId: Long?,
        cursorId: Long?
    ): CurationCursorPageResult {
        val page = curationProductRepository.findAllWithCursorPage(
            curationId = curation.id,
            cursorId = cursorId,
        )

        val badgeMap = if (userId != null) {
            val productIds = page.content.mapNotNull { it.product.id }

            userProductBadgeRepository.findByUserIdAndProductIds(userId, productIds)
                .associateBy { it.sk.removePrefix("PRODUCT#").toLong() }
        } else {
            emptyMap()
        }

        return CurationCursorPageResult.of(curation = curation, page = page, badgeMap = badgeMap)
    }

    override fun findProductsWithOffsetPage(curation: Curation, pageable: Pageable): CurationOffsetPageResult {
        val page = curationProductRepository.findAllWithOffsetPage(
            curationId = curation.id,
            pageable = pageable,
        )
        return CurationOffsetPageResult.of(curation = curation, page = page)
    }
}
