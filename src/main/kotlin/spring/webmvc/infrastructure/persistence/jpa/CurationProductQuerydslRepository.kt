package spring.webmvc.infrastructure.persistence.jpa

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import spring.webmvc.domain.model.entity.CurationProduct
import spring.webmvc.domain.model.entity.QCurationProduct.curationProduct

@Repository
class CurationProductQuerydslRepository(
    private val jpaQueryFactory: JPAQueryFactory,
) {
    fun findAllByCurationId(pageable: Pageable, curationId: Long): Page<CurationProduct> {
        val count = jpaQueryFactory
            .select(curationProduct.count())
            .from(curationProduct)
            .where(eqCurationId(curationId))
            .fetchOne() ?: 0L

        val content = jpaQueryFactory
            .selectFrom(curationProduct)
            .where(eqCurationId(curationId))
            .limit(pageable.pageSize.toLong())
            .offset(pageable.offset)
            .orderBy(curationProduct.sortOrder.asc())
            .fetch()

        return PageImpl(content, pageable, count)
    }

    private fun eqCurationId(curationId: Long) = curationProduct.curation.id.eq(curationId)
}