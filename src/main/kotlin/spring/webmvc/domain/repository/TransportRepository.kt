package spring.webmvc.domain.repository

import spring.webmvc.domain.model.entity.Transport

interface TransportRepository {
    fun findById(id: Long): Transport
    fun save(transport: Transport): Transport
    fun delete(transport: Transport)
}
