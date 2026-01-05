package spring.webmvc.infrastructure.persistence.jpa

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import spring.webmvc.domain.model.entity.Order
import spring.webmvc.domain.model.entity.QOrder.order
import spring.webmvc.domain.model.entity.QUser.user
import spring.webmvc.domain.model.enums.OrderStatus
import spring.webmvc.infrastructure.persistence.dto.CursorPage
import java.time.Instant

@Repository
class OrderQuerydslRepository(
    private val jpaQueryFactory: JPAQueryFactory,
) {
    companion object {
        const val DEFAULT_PAGE_SIZE = 10L
    }

    fun findAllWithOffsetPage(
        pageable: Pageable,
        userId: Long?,
        orderStatus: OrderStatus?,
        orderedFrom: Instant?,
        orderedTo: Instant?,
    ): Page<Order> {
        val count = jpaQueryFactory
            .select(order.count())
            .from(order)
            .join(order.user, user)
            .where(
                eqUserId(userId = userId),
                eqOrderStatus(orderStatus = orderStatus),
                goeOrderedAt(orderedFrom = orderedFrom),
                loeOrderedAt(orderedTo = orderedTo)
            )
            .fetchOne() ?: 0L

        val content = jpaQueryFactory
            .selectFrom(order)
            .join(order.user, user).fetchJoin()
            .where(
                eqUserId(userId = userId),
                eqOrderStatus(orderStatus = orderStatus),
                goeOrderedAt(orderedFrom = orderedFrom),
                loeOrderedAt(orderedTo = orderedTo)
            )
            .limit(pageable.pageSize.toLong())
            .offset(pageable.offset)
            .fetch()

        return PageImpl(content, pageable, count)
    }

    fun findAllWithCursorPage(
        cursorId: Long?,
        userId: Long?,
        orderStatus: OrderStatus?,
        orderedFrom: Instant?,
        orderedTo: Instant?,
    ): CursorPage<Order> {
        val content = jpaQueryFactory
            .selectFrom(order)
            .join(order.user, user).fetchJoin()
            .where(
                loeOrderId(cursorId = cursorId),
                eqUserId(userId = userId),
                eqOrderStatus(orderStatus = orderStatus),
                goeOrderedAt(orderedFrom = orderedFrom),
                loeOrderedAt(orderedTo = orderedTo),
            )
            .limit(DEFAULT_PAGE_SIZE + 1)
            .fetch()

        return CursorPage.create(content = content, size = DEFAULT_PAGE_SIZE) { it.id }
    }

    private fun loeOrderId(cursorId: Long?) = cursorId?.let { order.id.loe(cursorId) }

    private fun eqUserId(userId: Long?) = userId?.let { order.user.id.eq(it) }

    private fun eqOrderStatus(orderStatus: OrderStatus?) = orderStatus?.let { order.status.eq(it) }

    private fun goeOrderedAt(orderedFrom: Instant?) = orderedFrom?.let { order.orderedAt.goe(it) }

    private fun loeOrderedAt(orderedTo: Instant?) = orderedTo?.let { order.orderedAt.loe(it) }
}