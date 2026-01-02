package spring.webmvc.infrastructure.persistence.jpa

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.model.entity.QProduct.product
import spring.webmvc.infrastructure.persistence.dto.CursorPage

@Repository
class ProductQuerydslRepository(
    private val jpaQueryFactory: JPAQueryFactory,
) {
    fun findAll(cursorId: Long?, size: Int, name: String?): CursorPage<Product> {
        val content = jpaQueryFactory
            .selectFrom(product)
            .where(loeProductId(cursorId), likeName(name))
            .orderBy(product.id.desc())
            .limit(size.toLong() + 1)
            .fetch()

        return CursorPage.create(content = content, size = size) { it.id }
    }

    private fun loeProductId(cursorId: Long?) = if (cursorId == null) null else product.id.loe(cursorId)

    private fun likeName(name: String?) = if (name.isNullOrBlank()) null else product.name.like("%$name%")
}