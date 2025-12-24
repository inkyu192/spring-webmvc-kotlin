package spring.webmvc.infrastructure.persistence.adapter

import org.springframework.stereotype.Component
import spring.webmvc.domain.model.entity.Transport
import spring.webmvc.domain.repository.TransportRepository
import spring.webmvc.infrastructure.extensions.findByIdOrThrow
import spring.webmvc.infrastructure.persistence.jpa.TransportJpaRepository

@Component
class TransportRepositoryAdapter(
    private val jpaRepository: TransportJpaRepository,
) : TransportRepository {
    override fun findById(id: Long): Transport = jpaRepository.findByIdOrThrow(id)

    override fun save(transport: Transport) = jpaRepository.save(transport)

    override fun delete(transport: Transport) {
        jpaRepository.delete(transport)
    }
}
