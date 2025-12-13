package spring.webmvc.infrastructure.persistence.adapter

import org.springframework.stereotype.Component
import spring.webmvc.domain.repository.PermissionRepository
import spring.webmvc.infrastructure.persistence.jpa.PermissionJpaRepository

@Component
class PermissionRepositoryAdapter(
    private val jpaRepository: PermissionJpaRepository,
) : PermissionRepository {
    override fun findAllById(ids: Iterable<Long>) = jpaRepository.findAllById(ids)
}