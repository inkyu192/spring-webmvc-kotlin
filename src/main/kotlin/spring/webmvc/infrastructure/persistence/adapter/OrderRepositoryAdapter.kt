package spring.webmvc.infrastructure.persistence.adapter

import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import spring.webmvc.domain.model.entity.Order
import spring.webmvc.domain.model.enums.OrderStatus
import spring.webmvc.domain.repository.OrderRepository
import spring.webmvc.infrastructure.persistence.OrderJpaRepository
import spring.webmvc.infrastructure.persistence.OrderQuerydslRepository

@Component
class OrderRepositoryAdapter(
    private val jpaRepository: OrderJpaRepository,
    private val querydslRepository: OrderQuerydslRepository,
) : OrderRepository {
    override fun findAll(pageable: Pageable, memberId: Long?, orderStatus: OrderStatus?) =
        querydslRepository.findAll(pageable, memberId, orderStatus)

    override fun findByIdOrNull(id: Long) = jpaRepository.findByIdOrNull(id)

    override fun save(order: Order) = jpaRepository.save(order)
}