package spring.webmvc.infrastructure.persistence.adapter

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import spring.webmvc.domain.repository.PermissionRepository
import spring.webmvc.infrastructure.persistence.PermissionJpaRepository

@Component
class PermissionRepositoryAdapter(
    private val jpaRepository: PermissionJpaRepository
) : PermissionRepository {
    override fun findByIdOrNull(id: Long) = jpaRepository.findByIdOrNull(id)
}