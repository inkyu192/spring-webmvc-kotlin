package spring.webmvc.infrastructure.persistence.jpa

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import spring.webmvc.domain.model.entity.Order
import spring.webmvc.domain.model.entity.QOrder.order
import spring.webmvc.domain.model.entity.User
import spring.webmvc.domain.model.enums.OrderStatus

@Repository
class OrderQuerydslRepository(
    private val jpaQueryFactory: JPAQueryFactory,
) {
    fun findAll(pageable: Pageable, user: User?, orderStatus: OrderStatus?): Page<Order> {
        val count = jpaQueryFactory
            .select(order.count())
            .from(order)
            .join(order.user, spring.webmvc.domain.model.entity.QUser.user)
            .where(
                eqUser(user = user),
                eqOrderStatus(orderStatus = orderStatus)
            )
            .fetchOne() ?: 0L

        val content = jpaQueryFactory
            .selectFrom(order)
            .join(order.user, spring.webmvc.domain.model.entity.QUser.user).fetchJoin()
            .where(
                eqUser(user = user),
                eqOrderStatus(orderStatus = orderStatus)
            )
            .limit(pageable.pageSize.toLong())
            .offset(pageable.offset)
            .fetch()

        return PageImpl(content, pageable, count)
    }

    private fun eqUser(user: User?) = user?.let { order.user.eq(it) }

    private fun eqOrderStatus(orderStatus: OrderStatus?) = orderStatus?.let { order.status.eq(it) }
}