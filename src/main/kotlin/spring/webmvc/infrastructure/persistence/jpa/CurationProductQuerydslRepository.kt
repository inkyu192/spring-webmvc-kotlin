package spring.webmvc.infrastructure.persistence.jpa

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import spring.webmvc.domain.model.entity.Curation
import spring.webmvc.domain.model.entity.CurationProduct
import spring.webmvc.domain.model.entity.QCurationProduct.curationProduct
import spring.webmvc.domain.model.entity.QProduct.product
import spring.webmvc.infrastructure.persistence.dto.CursorPage

@Repository
class CurationProductQuerydslRepository(
    private val jpaQueryFactory: JPAQueryFactory,
) {
    fun findAll(curation: Curation, cursorId: Long?): CursorPage<CurationProduct> {
        val size = 10
        val content = jpaQueryFactory
            .selectFrom(curationProduct)
            .join(curationProduct.curation, spring.webmvc.domain.model.entity.QCuration.curation).fetchJoin()
            .join(curationProduct.product, product).fetchJoin()
            .where(eqCuration(curation), loeCurationProductId(cursorId))
            .orderBy(curationProduct.id.desc())
            .limit(size.toLong() + 1)
            .fetch()

        return CursorPage.create(content = content, size = size) { it.id }
    }

    private fun eqCuration(curation: Curation) = curationProduct.curation.eq(curation)

    private fun loeCurationProductId(cursorId: Long?) =
        if (cursorId == null) null else curationProduct.id.loe(cursorId)
}