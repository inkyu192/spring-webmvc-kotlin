package spring.webmvc.infrastructure.persistence

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import spring.webmvc.domain.model.entity.Item
import spring.webmvc.domain.model.entity.QItem.item
import spring.webmvc.domain.model.entity.QOrderItem.orderItem

@Repository
class ItemQuerydslRepository(
    private val jpaQueryFactory: JPAQueryFactory,
) {
    fun findAll(pageable: Pageable, name: String?): Page<Item> {
        val count = jpaQueryFactory
            .select(item.count())
            .from(item)
            .where(likeName(name))
            .fetchOne() ?: 0

        val tuples = jpaQueryFactory
            .select(item, orderItem.count)
            .from(item)
            .leftJoin(orderItem).on(item.id.eq(orderItem.item.id))
            .where(likeName(name))
            .groupBy(item.id)
            .orderBy(orderItem.count.desc())
            .limit(pageable.pageSize.toLong())
            .offset(pageable.offset)
            .fetch()

        val content = tuples.map { it.get(0, Item::class.java) }

        return PageImpl(content, pageable, count)
    }

    private fun likeName(name: String?) = name.takeIf { !it.isNullOrBlank() }?.let { item.name.like("%$it%") }
}