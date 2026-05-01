package spring.webmvc.infrastructure.persistence.jpa

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.stereotype.Repository
import spring.webmvc.application.dto.query.ProductCursorPageQuery
import spring.webmvc.application.dto.query.ProductOffsetPageQuery
import spring.webmvc.domain.dto.CursorPage
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.model.entity.QProduct.product
import spring.webmvc.domain.model.entity.QProductTag.productTag
import spring.webmvc.domain.model.enums.ProductStatus

@Repository
class ProductQuerydslRepository(
    private val jpaQueryFactory: JPAQueryFactory,
) {
    companion object {
        const val DEFAULT_PAGE_SIZE = 10L
    }

    fun findAllWithCursorPage(query: ProductCursorPageQuery): CursorPage<Product> {
        val baseQuery = jpaQueryFactory
            .selectFrom(product)
            .distinct()

        if (query.tagIds.isNotEmpty()) {
            baseQuery.innerJoin(productTag).on(productTag.product.eq(product))
        }

        val content = baseQuery
            .where(
                loeProductId(query.cursorId),
                likeName(query.name),
                eqStatus(query.status),
                inTagIds(query.tagIds),
            )
            .orderBy(product.id.desc())
            .limit(DEFAULT_PAGE_SIZE + 1)
            .fetch()

        return CursorPage.create(content = content, size = DEFAULT_PAGE_SIZE) { it.id }
    }

    fun findAllWithOffsetPage(query: ProductOffsetPageQuery): Page<Product> {
        val countQuery = jpaQueryFactory
            .select(product.countDistinct())
            .from(product)

        val contentQuery = jpaQueryFactory
            .selectFrom(product)
            .distinct()

        if (query.tagIds.isNotEmpty()) {
            countQuery.innerJoin(productTag).on(productTag.product.eq(product))
            contentQuery.innerJoin(productTag).on(productTag.product.eq(product))
        }

        val count = countQuery
            .where(
                likeName(query.name),
                eqStatus(query.status),
                inTagIds(query.tagIds),
            )
            .fetchOne() ?: 0L

        val content = contentQuery
            .where(
                likeName(query.name),
                eqStatus(query.status),
                inTagIds(query.tagIds),
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

    private fun inTagIds(tagIds: List<Long>) =
        if (tagIds.isEmpty()) null else productTag.tag.id.`in`(tagIds)
}
