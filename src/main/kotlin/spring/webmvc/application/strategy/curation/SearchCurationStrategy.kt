package spring.webmvc.application.strategy.curation

import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import spring.webmvc.application.dto.query.ProductCursorPageQuery
import spring.webmvc.application.dto.query.ProductOffsetPageQuery
import spring.webmvc.application.dto.result.CurationCursorPageResult
import spring.webmvc.application.dto.result.CurationOffsetPageResult
import spring.webmvc.application.dto.result.CurationProductResult
import spring.webmvc.domain.dto.CursorPage
import spring.webmvc.domain.model.entity.Curation
import spring.webmvc.domain.model.enums.CurationType
import spring.webmvc.domain.model.enums.ProductStatus
import spring.webmvc.domain.repository.ProductRepository
import spring.webmvc.domain.repository.UserProductBadgeRepository

@Component
class SearchCurationStrategy(
    private val productRepository: ProductRepository,
    private val userProductBadgeRepository: UserProductBadgeRepository,
) : CurationProductStrategy {

    override fun type(): CurationType = CurationType.SEARCH

    override fun findProductsWithCursorPage(
        curation: Curation,
        userId: Long?,
        cursorId: Long?
    ): CurationCursorPageResult {
        val keyword = curation.attribute.keyword
            ?: return emptyCursorResult(curation)

        val query = ProductCursorPageQuery(
            cursorId = cursorId,
            name = keyword,
            status = ProductStatus.SELLING,
        )

        val page = productRepository.findAllWithCursorPage(query)

        val badgeMap = if (userId != null) {
            val productIds = page.content.mapNotNull { it.id }

            userProductBadgeRepository.findByUserIdAndProductIds(userId, productIds)
                .associateBy { it.sk.removePrefix("PRODUCT#").toLong() }
        } else {
            emptyMap()
        }

        return CurationCursorPageResult.of(curation = curation, products = page.content, badgeMap = badgeMap)
    }

    override fun findProductsWithOffsetPage(curation: Curation, pageable: Pageable): CurationOffsetPageResult {
        val keyword = curation.attribute.keyword
            ?: return CurationOffsetPageResult.empty(curation)

        val query = ProductOffsetPageQuery(
            pageable = pageable,
            name = keyword,
            status = null,
        )
        val page = productRepository.findAllWithOffsetPage(query)
        return CurationOffsetPageResult.ofProducts(curation = curation, page = page)
    }

    private fun emptyCursorResult(curation: Curation) = CurationCursorPageResult(
        id = checkNotNull(curation.id),
        title = curation.title,
        placement = curation.placement,
        type = curation.type,
        exposureAttribute = spring.webmvc.application.dto.result.CurationExposureAttributeResult.of(curation.exposureAttribute),
        productPage = CursorPage(
            content = emptyList<CurationProductResult>(),
            size = 0,
            hasNext = false,
            nextCursorId = null,
        ),
    )
}
