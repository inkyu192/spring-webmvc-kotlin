package spring.webmvc.infrastructure.persistence.jpa

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import spring.webmvc.domain.model.entity.CurationProduct
import spring.webmvc.domain.model.entity.QCuration.curation
import spring.webmvc.domain.model.entity.QCurationProduct.curationProduct
import spring.webmvc.domain.model.entity.QProduct.product
import spring.webmvc.infrastructure.persistence.dto.CursorPage

@Repository
class CurationProductQuerydslRepository(
    private val jpaQueryFactory: JPAQueryFactory,
) {
    companion object {
        const val DEFAULT_PAGE_SIZE = 10L
    }

    fun findAllWithCursorPage(curationId: Long?, cursorId: Long?): CursorPage<CurationProduct> {
        val content = jpaQueryFactory
            .selectFrom(curationProduct)
            .join(curationProduct.curation, curation).fetchJoin()
            .join(curationProduct.product, product).fetchJoin()
            .where(eqCurationId(curationId), loeCurationProductId(cursorId))
            .orderBy(curationProduct.id.desc())
            .limit(DEFAULT_PAGE_SIZE + 1)
            .fetch()

        return CursorPage.create(content = content, size = DEFAULT_PAGE_SIZE) { it.id }
    }

    private fun eqCurationId(curationId: Long?) = curationId?.let { curationProduct.curation.id.eq(curationId) }

    private fun loeCurationProductId(cursorId: Long?) = cursorId?.let { curationProduct.id.loe(cursorId) }
}