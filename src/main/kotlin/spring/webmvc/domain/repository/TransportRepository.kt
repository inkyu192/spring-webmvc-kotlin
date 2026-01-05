package spring.webmvc.domain.repository

import spring.webmvc.domain.model.entity.Transport

interface TransportRepository {
    fun findByProductId(productId: Long): Transport
    fun save(transport: Transport): Transport
    fun delete(transport: Transport)
}
