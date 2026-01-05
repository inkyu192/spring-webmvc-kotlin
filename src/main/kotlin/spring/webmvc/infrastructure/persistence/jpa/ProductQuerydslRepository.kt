package spring.webmvc.infrastructure.persistence.jpa

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.stereotype.Repository
import spring.webmvc.application.dto.query.ProductCursorPageQuery
import spring.webmvc.application.dto.query.ProductOffsetPageQuery
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.model.entity.QProduct.product
import spring.webmvc.domain.model.enums.ProductStatus
import spring.webmvc.infrastructure.persistence.dto.CursorPage

@Repository
class ProductQuerydslRepository(
    private val jpaQueryFactory: JPAQueryFactory,
) {
    companion object {
        const val DEFAULT_PAGE_SIZE = 10L
    }

    fun findAllWithCursorPage(query: ProductCursorPageQuery): CursorPage<Product> {
        val content = jpaQueryFactory
            .selectFrom(product)
            .where(
                loeProductId(query.cursorId),
                likeName(query.name),
                eqStatus(query.status),
            )
            .orderBy(product.id.desc())
            .limit(DEFAULT_PAGE_SIZE + 1)
            .fetch()

        return CursorPage.create(content = content, size = DEFAULT_PAGE_SIZE) { it.id }
    }

    fun findAllWithOffsetPage(query: ProductOffsetPageQuery): Page<Product> {
        val count = jpaQueryFactory
            .select(product.count())
            .from(product)
            .where(
                likeName(query.name),
                eqStatus(query.status),
            )
            .fetchOne() ?: 0L

        val content = jpaQueryFactory
            .selectFrom(product)
            .where(
                likeName(query.name),
                eqStatus(query.status),
            )
            .orderBy(product.id.desc())
            .limit(query.pageable.pageSize.toLong())
            .offset(query.pageable.offset)
            .fetch()

        return PageImpl(content, query.pageable, count)
    }

    private fun loeProductId(cursorId: Long?) = cursorId?.let { product.id.loe(cursorId) }

    private fun likeName(name: String?) = name?.let { product.name.like("%$name%") }

    private fun eqStatus(status: ProductStatus?) = status?.let { product.status.eq(status) }
}