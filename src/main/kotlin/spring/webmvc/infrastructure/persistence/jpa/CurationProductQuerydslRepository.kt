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
    fun findAll(curationId: Long, cursorId: Long?, size: Int): CursorPage<CurationProduct> {
        val content = jpaQueryFactory
            .selectFrom(curationProduct)
            .join(curationProduct.curation, curation).fetchJoin()
            .join(curationProduct.product, product).fetchJoin()
            .where(eqCurationId(curationId), loeCurationProductId(cursorId))
            .orderBy(curationProduct.id.desc())
            .limit(size.toLong() + 1)
            .fetch()

        return CursorPage(content = content, size = size) { it.id }
    }

    private fun eqCurationId(curationId: Long) = curationProduct.curation.id.eq(curationId)

    private fun loeCurationProductId(cursorId: Long?) =
        if (cursorId == null) null else curationProduct.id.loe(cursorId)
}