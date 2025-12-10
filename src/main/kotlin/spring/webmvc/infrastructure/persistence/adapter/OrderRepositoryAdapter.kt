package spring.webmvc.infrastructure.persistence.adapter

import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import spring.webmvc.domain.model.entity.Order
import spring.webmvc.domain.model.enums.OrderStatus
import spring.webmvc.domain.repository.OrderRepository
import spring.webmvc.infrastructure.extensions.findByIdOrThrow
import spring.webmvc.infrastructure.persistence.jpa.OrderJpaRepository
import spring.webmvc.infrastructure.persistence.jpa.OrderQuerydslRepository

@Component
class OrderRepositoryAdapter(
    private val jpaRepository: OrderJpaRepository,
    private val querydslRepository: OrderQuerydslRepository,
) : OrderRepository {
    override fun findAll(pageable: Pageable, userId: Long?, orderStatus: OrderStatus?) =
        querydslRepository.findAll(pageable = pageable, userId = userId, orderStatus = orderStatus)

    override fun findById(id: Long): Order = jpaRepository.findByIdOrThrow(id = id)

    override fun findByIdAndUserId(id: Long, userId: Long) =
        jpaRepository.findByIdAndUserId(id = id, userId = userId)

    override fun save(order: Order) = jpaRepository.save(order)
}