package spring.webmvc.infrastructure.persistence

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.model.entity.QOrderProduct.orderProduct
import spring.webmvc.domain.model.entity.QProduct.product

@Repository
class ProductQuerydslRepository(
    private val jpaQueryFactory: JPAQueryFactory,
) {
    fun findAll(pageable: Pageable, name: String?): Page<Product> {
        val count = jpaQueryFactory
            .select(product.count())
            .from(product)
            .where(likeName(name))
            .fetchOne() ?: 0L

        val content = jpaQueryFactory
            .selectFrom(product)
            .leftJoin(orderProduct).on(product.id.eq(orderProduct.product.id))
            .where(likeName(name))
            .groupBy(product.id)
            .orderBy(orderProduct.count().desc())
            .limit(pageable.pageSize.toLong())
            .offset(pageable.offset)
            .fetch()

        return PageImpl(content, pageable, count)
    }

    private fun likeName(name: String?) = name.takeIf { !it.isNullOrBlank() }?.let { product.name.like("%$it%") }
}