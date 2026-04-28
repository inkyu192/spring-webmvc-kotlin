package spring.webmvc.application.strategy.curation

import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import spring.webmvc.application.dto.result.CurationCursorPageResult
import spring.webmvc.application.dto.result.CurationOffsetPageResult
import spring.webmvc.domain.dto.CursorPage
import spring.webmvc.domain.model.entity.Curation
import spring.webmvc.domain.model.enums.CurationType
import spring.webmvc.domain.repository.ProductRepository
import spring.webmvc.domain.repository.UserCurationProductRepository
import spring.webmvc.domain.repository.UserProductBadgeRepository

@Component
class PersonalizedCurationStrategy(
    private val userCurationProductRepository: UserCurationProductRepository,
    private val productRepository: ProductRepository,
    private val userProductBadgeRepository: UserProductBadgeRepository,
) : CurationProductStrategy {

    override fun type(): CurationType = CurationType.PERSONALIZED

    override fun findProductsWithCursorPage(
        curation: Curation,
        userId: Long?,
        cursorId: Long?
    ): CurationCursorPageResult {
        if (userId == null) {
            return emptyCursorResult(curation)
        }

        val userCurationProduct = userCurationProductRepository.findByUserIdAndCurationId(
            userId = userId,
            curationId = checkNotNull(curation.id),
        ) ?: return emptyCursorResult(curation)

        val productIds = userCurationProduct.productIds
        val productMap = productRepository.findAllById(productIds)
            .associateBy { checkNotNull(it.id) }

        val products = productIds.mapNotNull { productMap[it] }

        val badgeMap = userProductBadgeRepository.findByUserIdAndProductIds(userId, productIds)
            .associateBy { it.sk.removePrefix("PRODUCT#").toLong() }

        return CurationCursorPageResult.of(curation = curation, products = products, badgeMap = badgeMap)
    }

    override fun findProductsWithOffsetPage(curation: Curation, pageable: Pageable): CurationOffsetPageResult {
        return CurationOffsetPageResult.empty(curation)
    }

    private fun emptyCursorResult(curation: Curation) = CurationCursorPageResult(
        id = checkNotNull(curation.id),
        title = curation.title,
        placement = curation.placement,
        type = curation.type,
        exposureAttribute = spring.webmvc.application.dto.result.CurationExposureAttributeResult.of(curation.exposureAttribute),
        productPage = CursorPage(
            content = emptyList(),
            size = 0,
            hasNext = false,
            nextCursorId = null,
        ),
    )
}
